package com.example.drawappcompose.models

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import com.google.firebase.Timestamp

data class Draws(
    val userId: String = "",
    val title: String = "",
    val drawImage: ImageBitmap? = null,
    val timestamp: Timestamp = Timestamp.now(),
    val documentId: String = "",
)
