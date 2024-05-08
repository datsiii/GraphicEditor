package com.example.drawappcompose.repository

import com.example.drawappcompose.models.Draws
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

const val DRAWS_COLLECTION_REF = "draws"

class StorageRepository() {
    val user = Firebase.auth.currentUser
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
                onSuccess.invoke(it.toObject(Draws::class.java))
            }
            .addOnFailureListener { result ->
                onError.invoke(result.cause)
            }
    }

    fun addDraw(
        userId: String = "",
        title: String = "",
        //image:,
        timestamp: Timestamp = com.google.firebase.Timestamp.now(),
        //colorIndex: Int = 0,
        onComplete: (Boolean) -> Unit,
    ) {
        val documentId = drawsRef.document().id
        val draw = Draws(
            userId,
            title,
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
        draw: String,
        drawId: String,
        onResult: (Boolean) -> Unit
    ) {
        val updateData = hashMapOf<String, Any>(
            "title" to title,
            //......
        )
        drawsRef.document(drawId)
            .update(updateData)
            .addOnCompleteListener {
                onResult(it.isSuccessful)
            }

    }

}

sealed class Resources<T>(
    val data: T? = null,
    val throwable: Throwable? = null,
) {
    class Loading<T> : Resources<T>()
    class Success<T>(data: T?) : Resources<T>(data = data)
    class Error<T>(throwable: Throwable?) : Resources<T>(throwable = throwable)
}