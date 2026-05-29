package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.data.remote.dto.SubmitReviewRequest
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RateTripViewModel : ViewModel() {
    private val repo = ServiceLocator.operationsRepo

    private val _state = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val state = _state.asStateFlow()

    fun submit(targetUserId: String, reservationId: String?, routeId: String?, rating: Int, comment: String) {
        if (rating <= 0) {
            _state.value = UiState.Error("Asigna una calificación")
            return
        }
        _state.value = UiState.Loading
        viewModelScope.launch {
            repo.submitReview(SubmitReviewRequest(targetUserId, reservationId, routeId, rating, comment))
                .onSuccess { _state.value = UiState.Success(Unit) }
                .onFailure { _state.value = UiState.Error(it.friendly()) }
        }
    }
}
