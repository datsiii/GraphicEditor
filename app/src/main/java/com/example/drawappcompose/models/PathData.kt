package com.example.drawappcompose.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap

data class PathData(
    val path: Path = Path(),
    val color: Color = Color.Black,
    val lineWidth: Float = 5f,
    val cap: StrokeCap = StrokeCap.Round,
    val alpha: Float = 1f
){
    constructor() : this(Path(), Color.Black, 5f, StrokeCap.Round, 1f)
}
