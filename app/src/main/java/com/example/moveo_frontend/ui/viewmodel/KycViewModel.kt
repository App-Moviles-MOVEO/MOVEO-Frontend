package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class KycViewModel : ViewModel() {
    private val repo = ServiceLocator.authRepo

    private val _state = MutableStateFlow<UiState<String>>(UiState.Idle)
    val state = _state.asStateFlow()

    fun submit(dniFront: File, dniBack: File, selfie: File) {
        _state.value = UiState.Loading
        viewModelScope.launch {
            repo.uploadKyc(dniFront, dniBack, selfie)
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.friendly()) }
        }
    }
}
