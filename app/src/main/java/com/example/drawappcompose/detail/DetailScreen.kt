package com.example.drawappcompose.detail

import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.example.drawappcompose.models.DetailUiState
//import androidx.compose.material.ScaffoldState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.example.drawappcompose.Navigation
import com.example.drawappcompose.models.PathData
import com.example.drawappcompose.ui.BottomPanel
import com.example.drawappcompose.ui.theme.DrawAppComposeTheme
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.CaptureController
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeApi::class)
@Composable
fun DetailScreen(
    detailViewModel: DetailViewModel?, drawId: String, onNavigate: () -> Unit
) {
    val context = LocalContext.current
    val detailUiState = detailViewModel?.detailUiState ?: DetailUiState()
    val isFormsNotBlank = detailUiState.title.isNotBlank()
    val isDrawIdNotBlank = drawId.isNotBlank()
    val icon = if (isDrawIdNotBlank) Icons.Default.Refresh else Icons.Default.Check
    LaunchedEffect(key1 = Unit) {
        if (drawId.isNotBlank()) {
            detailViewModel?.getDraw(drawId)
        } else {
            detailViewModel?.resetState()
        }
    }
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState() //can't find rememberScaffoldState()
    val pathData = remember {
        mutableStateOf(PathData())
    }
    val pathList = remember {
        mutableStateListOf(PathData())
    }

    fun saveBitmapToStorage(bitmap: Bitmap) {
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
                    context, arrayOf(file.absolutePath), arrayOf("image/jpeg"), null
                )

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun capture(captureController: CaptureController): Bitmap?{
        var bitmap: Bitmap? = null
        scope.launch {
            val bitmapAsync = captureController.captureAsync()
            try {
                val imageBitmap = bitmapAsync.await()
                Toast.makeText(context, "download", Toast.LENGTH_SHORT).show()
                // Do something with `bitmap`.
                bitmap = imageBitmap.asAndroidBitmap()
                saveBitmapToStorage(bitmap!!)
            } catch (error: Throwable) {
                // Error occurred, do something.
                Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
            }
        }
        return bitmap
    }

    Scaffold(
        //scaffoldState = scaffoldState
        floatingActionButton = {
            AnimatedVisibility(visible = true) {
                FloatingActionButton(
                    onClick = {
                        if (isDrawIdNotBlank) {
                            detailViewModel?.updateDraw(drawId)
                        } else {
                            detailViewModel?.addDraw()
                        }
                    }
                ) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            }
        }

    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(padding)
        ) {
            if (detailUiState.drawsAddedStatus) {
                scope.launch {//это обман, оно запускается
                    scaffoldState.snackbarHostState.showSnackbar("Added draw Successfully")
                    detailViewModel?.resetDrawAddedStatus()
                    onNavigate.invoke()
                }
            }

            if (detailUiState.drawsUpdatedStatus) {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar("Draw updated Successfully")
                    detailViewModel?.resetDrawAddedStatus()
                    onNavigate.invoke()
                }
            }

            val captureController = DrawCanvas(pathData, pathList)

            BottomPanel({ color ->
                pathData.value = pathData.value.copy(
                    color = color
                )
            }, { lineWidth ->
                pathData.value = pathData.value.copy(
                    lineWidth = lineWidth
                )
            }, { alpha ->
                pathData.value = pathData.value.copy(
                    alpha = alpha
                )

            }, { ->
                pathList.removeIf { pathD ->
                    pathList[pathList.size - 1] == pathD

                }
            },

                { cap ->
                    pathData.value = pathData.value.copy(
                        cap = cap
                    )

                }) {
                // Capture content
                val capturedBitmap = capture(captureController)
                if (capturedBitmap != null) {
                    detailViewModel?.onDrawImageChange(capturedBitmap.asImageBitmap())
                }
            }

        }

    }


}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawCanvas(
    pathData: MutableState<PathData>, pathList: SnapshotStateList<PathData>
): CaptureController {
    var tempPath = Path()
    val captureController = rememberCaptureController()
    Column(modifier = Modifier.capturable(captureController)) {
        Canvas(modifier = Modifier/*.drawWithContent {  }*/
            .background(Color.White)
            .fillMaxWidth()
            .fillMaxHeight(0.73f)
            .pointerInput(true) {
                detectDragGestures(onDragStart = {
                    pathList.add(
                        pathData.value.copy(
                            path = tempPath
                        )
                    )
                    tempPath = Path()
                }, onDragEnd = {
                    pathList.add(
                        pathData.value.copy(
                            path = tempPath
                        )
                    )
                }) { change, dragAmount ->
                    tempPath.moveTo(
                        change.position.x - dragAmount.x, change.position.y - dragAmount.y
                    )
                    tempPath.lineTo(
                        change.position.x, change.position.y
                    )
                    if (pathList.size > 0) {
                        pathList.removeAt(pathList.size - 1)
                    }
                    pathList.add(
                        pathData.value.copy(
                            path = tempPath
                        )
                    )
                }
            }) {
            pathList.forEach { pathData ->
                drawPath(
                    pathData.path, color = pathData.color, style = Stroke(
                        pathData.lineWidth, cap = pathData.cap
                    ), alpha = pathData.alpha
                )
            }

        }
    }
    return captureController

}