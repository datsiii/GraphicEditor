package com.example.drawappcompose.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.drawappcompose.R
import com.example.drawappcompose.models.Draws
import com.example.drawappcompose.models.PathData
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import android.util.Base64
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.drawappcompose.Utils.imageBitmapToBase64
import java.io.ByteArrayOutputStream



const val DRAWS_COLLECTION_REF = "draws"



class StorageRepository() {
    fun user() = Firebase.auth.currentUser
    fun hasUser(): Boolean = Firebase.auth.currentUser != null

    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()

    private val drawsRef: CollectionReference = Firebase
        .firestore.collection(DRAWS_COLLECTION_REF)

    fun getUserDraws(
        userId: String
    ): Flow<Resources<List<Draws>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null

        try {
            snapshotStateListener = drawsRef
                .orderBy("timestamp")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val draws = snapshot.toObjects(Draws::class.java)
                        Resources.Success(data = draws)
                    } else {
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)
                }

        } catch (e: Exception) {
            trySend(Resources.Error(e.cause))
            e.printStackTrace()
        }
        awaitClose {
            snapshotStateListener?.remove()
        }
    }

    fun getDraw(
        drawId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (Draws?) -> Unit
    ) {
        drawsRef
            .document(drawId)
            .get()
            .addOnSuccessListener {
                val draw = it.toObject(Draws::class.java)
                onSuccess.invoke(draw)
            }
            .addOnFailureListener { result ->
                onError.invoke(result.cause)
            }
    }

    fun addDraw(
        userId: String = "",
        title: String = "",
        drawImage: ImageBitmap? = null,
        pathData: PathData? = null,
        timestamp: Timestamp = com.google.firebase.Timestamp.now(),
        onComplete: (Boolean) -> Unit,
    ) {
        val documentId = drawsRef.document().id
        val drawImageBase64 = drawImage?.let { imageBitmapToBase64(it) }
        val draw = Draws(
            userId,
            title,
            drawImageBase64,
            //pathData,
            timestamp,
            documentId = documentId
        )
        drawsRef
            .document(documentId)
            .set(draw)
            .addOnCompleteListener { result ->
                onComplete.invoke(result.isSuccessful)
            }
    }

    fun deleteDraw(
        drawId: String,
        onComplete: (Boolean) -> Unit
    ) {
        drawsRef.document(drawId)
            .delete()
            .addOnCompleteListener {
                onComplete.invoke(it.isSuccessful)
            }
    }

    fun updateDraw(
        title: String,
        //draw: MutableState<PathData>?,
        drawImage: ImageBitmap?,
        //pathData: PathData?,
        drawId: String,
        onResult: (Boolean) -> Unit
    ) {
        val drawImageBase64 = drawImage?.let { imageBitmapToBase64(it) } ?: ""
        val updateData = hashMapOf<String, Any>(
            "title" to title,
            //"draw-data" to (draw?.value ?: ""),
            "drawImage" to (drawImageBase64 ?: ""),
            //"pathData" to (pathData ?: PathData())
        )
        drawsRef.document(drawId)
            .update(updateData)
            .addOnCompleteListener {
                onResult(it.isSuccessful)
            }

    }

    fun signOut() = Firebase.auth.signOut()

}

sealed class Resources<T>(
    val data: T? = null,
    val throwable: Throwable? = null,
) {
    class Loading<T> : Resources<T>()
    class Success<T>(data: T?) : Resources<T>(data = data)
    class Error<T>(throwable: Throwable?) : Resources<T>(throwable = throwable)
}