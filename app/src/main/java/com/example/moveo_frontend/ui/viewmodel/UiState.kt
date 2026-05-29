package com.example.moveo_frontend.ui.viewmodel

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

fun Throwable.friendly(): String = when (this) {
    is java.net.UnknownHostException -> "Sin conexión con el servidor"
    is java.net.SocketTimeoutException -> "El servidor tardó demasiado en responder"
    is retrofit2.HttpException -> "Error ${code()}: ${message()}"
    else -> message ?: "Ocurrió un error inesperado"
}
