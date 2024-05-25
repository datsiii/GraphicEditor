package com.example.drawappcompose.models

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap

data class DetailUiState(
    val title: String = "",
    val draw: MutableState<PathData>? = null,
    val drawImage: ImageBitmap? = null,
    val drawsAddedStatus: Boolean = false,
    val drawsUpdatedStatus: Boolean = false,
    val selectedDraw: Draws? = null
)
