package com.example.drawappcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import com.example.drawappcompose.ui.BottomPanel
import com.example.drawappcompose.ui.PathData
import com.example.drawappcompose.ui.theme.DrawAppComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val pathData = remember {
                mutableStateOf(PathData())
            }
            DrawAppComposeTheme {
                Column {
                    DrawCanvas(pathData)
                    BottomPanel(
                        {color ->
                            pathData.value = pathData.value.copy(
                                color = color
                            )
                        }
                    ){lineWidth ->
                        pathData.value = pathData.value.copy(
                            lineWidth = lineWidth
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DrawCanvas(pathData: MutableState<PathData>) {
    var tempPath = Path()
    val pathList = remember {
        mutableStateListOf(PathData())
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.75f)
            .pointerInput(true) {
                detectDragGestures(
                    onDragStart = {
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
                    if(pathList.size>0){
                        pathList.removeAt(pathList.size - 1)
                    }
                    pathList.add(
                        pathData.value.copy(
                            path = tempPath
                        )
                    )
                }
            }
    ){
        pathList.forEach{pathData ->
            drawPath(
                pathData.path,
                color = pathData.color,
                style = Stroke(
                    pathData.lineWidth/*,
                    cap = StrokeCap.Round*/)
            )
        }

    }

}

