package com.example.drawappcompose.detail

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.drawappcompose.models.DetailUiState
import com.example.drawappcompose.models.Draws
import com.example.drawappcompose.models.PathData
import com.example.drawappcompose.repository.StorageRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser

class DetailViewModel(
    private val repository: StorageRepository = StorageRepository(),
) : ViewModel() {
    var detailUiState by mutableStateOf(DetailUiState())
    private val hasUser: Boolean
        get() = repository.hasUser()

    private val user: FirebaseUser?
        get() = repository.user()

    fun onTitleChange(title: String) {
        detailUiState = detailUiState.copy(title = title)
    }

    fun onDrawChange(draw: MutableState<PathData>) {
        detailUiState = detailUiState.copy(draw = draw)
    }

    fun addDraw() {
        if (hasUser) {
            repository.addDraw(
                userId = user!!.uid,
                title = detailUiState.title,
                timestamp = Timestamp.now()
            ) {
                detailUiState = detailUiState.copy(drawsAddedStatus = it)
            }
        }
    }

    fun setEdit(draw: Draws) {
        detailUiState = detailUiState.copy(
            title = draw.title,
            //draw =
        )
    }

    fun getDraw(drawId: String) {
        repository.getDraw(
            drawId = drawId,
            onError = {},
        ) {
            detailUiState = detailUiState.copy(selectedDraw = it)
            detailUiState.selectedDraw?.let { it1 -> setEdit(it1) }
        }
    }

    fun updateDraw(
        drawId: String
    ) {
        repository.updateDraw(
            title = detailUiState.title,
            draw = detailUiState.draw,
            drawImage = detailUiState.drawImage,
            drawId = drawId
        ) {
            detailUiState = detailUiState.copy(drawsUpdatedStatus = it)
        }
    }

    fun resetDrawAddedStatus() {
        detailUiState = detailUiState.copy(
            drawsAddedStatus = false,
            drawsUpdatedStatus = false
        )
    }

    fun resetState() {
        detailUiState = DetailUiState()
    }


}
