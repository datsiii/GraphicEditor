package com.example.drawappcompose.models

import android.graphics.Bitmap
import com.google.firebase.Timestamp

data class Draws(
    val userId: String = "",
    val title: String = "",
    //val image: Bitmap,
    val timestamp: Timestamp = Timestamp.now(),
    val documentId: String = "",
)
