package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.data.remote.dto.ChatMessageDto
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val repo = ServiceLocator.operationsRepo

    private val _messages = MutableStateFlow<List<ChatMessageDto>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _state = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val state = _state.asStateFlow()

    fun load(peerId: String) {
        _state.value = UiState.Loading
        viewModelScope.launch {
            repo.chat(peerId)
                .onSuccess { _messages.value = it; _state.value = UiState.Success(Unit) }
                .onFailure { _state.value = UiState.Error(it.friendly()) }
        }
    }

    fun send(peerId: String, body: String) {
        if (body.isBlank()) return
        viewModelScope.launch {
            repo.send(peerId, body).onSuccess { msg ->
                _messages.value = _messages.value + msg
            }
        }
    }
}
