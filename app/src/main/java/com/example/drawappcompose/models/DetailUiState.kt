package com.example.drawappcompose.models

import androidx.compose.runtime.MutableState

data class DetailUiState(
    //val colorIdnex: Int = 0,
    val title: String = "",
    val draw: MutableState<PathData>? = null,
    val drawsAddedStatus: Boolean = false,
    val drawsUpdatedStatus: Boolean = false,
    val selectedDraw: Draws? = null
)
