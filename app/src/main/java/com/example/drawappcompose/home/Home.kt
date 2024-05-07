package com.example.drawappcompose.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.drawappcompose.data.PathData
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.CaptureController
import dev.shreyaspatil.capturable.controller.rememberCaptureController

@Composable
fun Home() {

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawCanvas(
    pathData: MutableState<PathData>,
    pathList: SnapshotStateList<PathData>
): CaptureController {
    var tempPath = Path()
    val captureController = rememberCaptureController()
    Column(modifier = Modifier.capturable(captureController)) {
        Canvas(
            modifier = Modifier
                /*.drawWithContent {  }*/
                .background(Color.White)
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .pointerInput(true) {
                    detectDragGestures(
                        onDragStart = {
                            pathList.add(
                                pathData.value.copy(
                                    path = tempPath
                                )
                            )
                            tempPath = Path()
                        },
                        onDragEnd = {
                            pathList.add(
                                pathData.value.copy(
                                    path = tempPath
                                )
                            )
                        }
                    ) { change, dragAmount ->
                        tempPath.moveTo(
                            change.position.x - dragAmount.x,
                            change.position.y - dragAmount.y
                        )
                        tempPath.lineTo(
                            change.position.x,
                            change.position.y
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
                }
        ) {
            pathList.forEach { pathData ->
                drawPath(
                    pathData.path,
                    color = pathData.color,
                    style = Stroke(
                        pathData.lineWidth,
                        cap = pathData.cap
                    ),
                    alpha = pathData.alpha
                )
            }

        }
    }
    return captureController

}