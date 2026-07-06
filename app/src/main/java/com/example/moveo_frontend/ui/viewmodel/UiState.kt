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
    is retrofit2.HttpException -> friendlyHttp(this)
    else -> message ?: "Ocurrió un error inesperado"
}

/** Códigos de negocio que devuelve el backend → mensajes para el usuario. */
private val backendErrorMessages = listOf(
    "no_seats_available" to "Ya no quedan asientos disponibles en esta ruta",
    "already_requested" to "Ya enviaste una solicitud para este viaje",
    "route_not_active" to "Esta ruta ya no está disponible",
    "not_institutional_email" to "Necesitas un correo institucional (@upc.edu.pe) para esta acción",
    "passenger_id_required" to "No pudimos identificar tu usuario. Vuelve a iniciar sesión",
    "refund_not_allowed" to "Según la política de cancelación no corresponde reembolso",
    "already_refunded" to "Este pago ya fue reembolsado",
    "rental_not_found" to "No encontramos la reserva",
    "no_completed_payment" to "La reserva aún no tiene un pago completado",
    "insufficient_balance" to "Saldo insuficiente para esta operación",
    "illegal_transition" to "El estado actual no permite esta acción",
    "not_route_owner" to "Solo el dueño de la ruta puede hacer esto"
)

private fun friendlyHttp(e: retrofit2.HttpException): String {
    // El cuerpo de error solo puede leerse una vez; se consume aquí para mapear el código.
    val body = runCatching { e.response()?.errorBody()?.string() }.getOrNull().orEmpty()
    backendErrorMessages.firstOrNull { body.contains(it.first) }?.let { return it.second }
    return "Error ${e.code()}: ${e.message()}"
}
