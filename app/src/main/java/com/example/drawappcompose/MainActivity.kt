package com.example.drawappcompose

import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drawappcompose.ui.BottomPanel
import com.example.drawappcompose.data.PathData
import com.example.drawappcompose.login.LoginViewModel
import com.example.drawappcompose.ui.theme.DrawAppComposeTheme
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.CaptureController
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import com.example.drawappcompose.home.DrawCanvas


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            /*val fs = Firebase.firestore
            fs.collection("draws")
                .document().set(mapOf("name" to "draw"))*/
            val loginViewModel = viewModel(modelClass = LoginViewModel::class.java)
            val scope = rememberCoroutineScope()
            val pathData = remember {
                mutableStateOf(PathData())
            }
            val pathList = remember {
                mutableStateListOf(PathData())
            }

            DrawAppComposeTheme {

                Column {
                    Navigation(loginViewModel = loginViewModel)
                    val captureController = DrawCanvas(pathData, pathList)
                    BottomPanel(
                        { color ->
                            pathData.value = pathData.value.copy(
                                color = color
                            )
                        },
                        { lineWidth ->
                            pathData.value = pathData.value.copy(
                                lineWidth = lineWidth
                            )
                        },
                        { alpha ->
                            pathData.value = pathData.value.copy(
                                alpha = alpha
                            )

                        },
                        { ->
                            pathList.removeIf { pathD ->
                                pathList[pathList.size - 1] == pathD

                            }
                        },

                        { cap ->
                            pathData.value = pathData.value.copy(
                                cap = cap
                            )

                        }
                    )
                    {
                        // Capture content
                        scope.launch {
                            val bitmapAsync = captureController.captureAsync()
                            try {
                                val imageBitmap = bitmapAsync.await()
                                Toast.makeText(this@MainActivity, "download", Toast.LENGTH_SHORT)
                                    .show()
                                // Do something with `bitmap`.
                                val bitmap = imageBitmap.asAndroidBitmap()
                                saveBitmapToStorage(bitmap)
                            } catch (error: Throwable) {
                                // Error occurred, do something.
                                Toast.makeText(this@MainActivity, "error", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveBitmapToStorage(bitmap: Bitmap) {
        val timestamp = System.currentTimeMillis() // Get current timestamp
        val filename = "image_$timestamp.jpg" // Construct filename with timestamp
        val externalDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        try {
            externalDir?.let { dir ->
                val file = File(dir, filename)
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()
                // Notify media scanner to update the media store
                MediaScannerConnection.scanFile(
                    this@MainActivity,
                    arrayOf(file.absolutePath),
                    arrayOf("image/jpeg"),
                    null
                )

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}



