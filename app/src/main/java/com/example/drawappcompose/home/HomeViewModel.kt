package com.example.drawappcompose.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawappcompose.models.HomeUiState
import com.example.drawappcompose.repository.Resources
import com.example.drawappcompose.repository.StorageRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: StorageRepository = StorageRepository()
) : ViewModel() {
    var homeUiState by mutableStateOf(HomeUiState())

    val user = repository.user()
    val hasUser: Boolean
        get() = repository.hasUser()
    private val userId: String
        get() = repository.getUserId()

    fun loadDraws() {
        if (hasUser) {
            if (userId.isNotBlank()) {
                getUserDraws(userId)
            }
        } else {
            homeUiState = homeUiState.copy(
                drawList = Resources.Error(
                    throwable = Throwable(message = "User is not Login")
                )
            )
        }
    }

    private fun getUserDraws(userId: String) = viewModelScope.launch {
        repository.getUserDraws(userId).collect {
            homeUiState = homeUiState.copy(drawList = it)
        }
    }

    fun deleteDraw(drawId: String) = repository.deleteDraw(drawId) {
        homeUiState = homeUiState.copy(drawDeletedStatus = it)
    }

    fun signOut() = repository.signOut()
}