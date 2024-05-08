package com.example.drawappcompose.models

import com.google.firebase.Timestamp

data class Draws(
    val userId: String = "",
    val title: String = "",
    //val image:,
    val timestamp: Timestamp = com.google.firebase.Timestamp.now(),
    //val colorIndex: Int = 0,
    val documentId: String = "",
)
