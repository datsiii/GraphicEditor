package com.example.drawappcompose.models

import com.example.drawappcompose.repository.Resources

data class HomeUiState(
    val drawList: Resources<List<Draws>> = Resources.Loading(),
    val drawDeletedStatus: Boolean = false
)
