package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.data.Review
import com.example.moveo_frontend.data.User
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val auth = ServiceLocator.authRepo
    private val ops = ServiceLocator.operationsRepo

    private val _user = MutableStateFlow<UiState<User>>(UiState.Loading)
    val user = _user.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews = _reviews.asStateFlow()

    init { load() }

    fun load() {
        _user.value = UiState.Loading
        viewModelScope.launch {
            auth.me()
                .onSuccess { _user.value = UiState.Success(it) }
                .onFailure { _user.value = UiState.Error(it.friendly()) }
            ops.myReviews().onSuccess { _reviews.value = it }
        }
    }
}
