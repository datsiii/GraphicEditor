package com.example.drawappcompose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.drawappcompose.R
import com.example.drawappcompose.ui.theme.LightPurple
import io.mhssn.colorpicker.ColorPicker
import io.mhssn.colorpicker.ColorPickerType

@Composable
fun BottomPanel(
    onClick: (Color) -> Unit,
    onLineWidthChange: (Float) -> Unit,
    onChangeTransparency: (Float) -> Unit,
    onBackClick: () -> Unit,
    onCapClick: (StrokeCap) -> Unit,
    onDownloadClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightPurple),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ColorList { color ->
            onClick(color)
        }
        CustomSlider({ lineWidth ->
            onLineWidthChange(lineWidth)
        }) { alpha ->
            onChangeTransparency(alpha)
        }
        ButtonPanel(
            {
                onBackClick()
            },
            {
                onDownloadClick()
            }
        ) { cap ->
            onCapClick(cap)
        }
        Spacer(modifier = Modifier.height(5.dp))
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ColorList(onClick: (Color) -> Unit) {
    var pickedColor = Color.Black
    /*ColorPicker(
        type = ColorPickerType.Ring(
            ringWidth = 10.dp,
            previewRadius = 80.dp,
            showAlphaBar = false,
            showColorPreview = true
        )
    ) {color ->
        pickedColor = color
    }*/
    val colors = listOf(
        Color.Red,
        Color.Yellow,
        Color.Green,
        Color.Blue,
        Color.White,
        Color.Black
    )
    LazyRow(
        modifier = Modifier.padding(10.dp)
    ) {
        items(colors) { color ->
            Box(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .clickable {
                        onClick(color)
                    }
                    .size(30.dp)
                    .background(color, CircleShape)
            )

        }
    }
}

@Composable
fun CustomSlider(
    onChangeWidth: (Float) -> Unit,
    onChangeTransparency: (Float) -> Unit
) {
    val fontFamily = FontFamily(
        Font(R.font.museomodernoblack, FontWeight.ExtraBold)
    )
    var positionw by remember {
        mutableStateOf(0.05f)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Line width: ${(positionw * 100).toInt()}", fontFamily = fontFamily, color = Color.DarkGray)
        Slider(
            value = positionw,
            onValueChange = {
                val tempPos = if (it > 0) it else 0.01f
                positionw = tempPos
                onChangeWidth(tempPos * 100)
            }
        )
    }
    var positiona by remember {
        mutableStateOf(1f)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Transparency: ${(positiona * 100).toInt()}", fontFamily = fontFamily, color = Color.DarkGray)
        Slider(
            value = positiona,
            onValueChange = {
                val tempPos = if (it > 0) it else 0.01f
                positiona = tempPos
                onChangeTransparency(tempPos)
            }
        )
    }
}

@Composable
fun ButtonPanel(
    onClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onCapClick: (StrokeCap) -> Unit
) {
    Row(
        Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxWidth(0.5f)
                .padding(start = 10.dp, end = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                /*modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White),*/
                onClick = {
                    onClick()
                }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
            IconButton(
                /*modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White),*/
                onClick = {
                    onDownloadClick()
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_download_24),
                    contentDescription = null
                )
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(end = 10.dp),
            horizontalArrangement = Arrangement.Absolute.Left
        ) {
            IconButton(
                /*modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White),*/
                onClick = {
                    onCapClick(StrokeCap.Round)
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_brush_24),
                    contentDescription = null
                )
            }
            IconButton(
                /*modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White),*/
                onClick = {
                    onCapClick(StrokeCap.Butt)
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_brush_24),
                    contentDescription = null
                )
            }
            IconButton(
                /*modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White),*/
                onClick = {
                    onCapClick(StrokeCap.Square)
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_brush_24),
                    contentDescription = null
                )
            }
        }
    }

}