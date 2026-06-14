package com.example.moveo_frontend.data.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val DAY_MS = 24L * 60 * 60 * 1000

/**
 * Fechas de alquiler elegidas por el usuario (millis UTC a medianoche, fin exclusivo).
 * Viven en memoria para que catálogo, detalle y pago compartan la misma selección.
 */
object RentalDateStore {
    private val _startMillis = MutableStateFlow<Long?>(null)
    val startMillis = _startMillis.asStateFlow()

    private val _endMillis = MutableStateFlow<Long?>(null)
    val endMillis = _endMillis.asStateFlow()

    fun set(start: Long, end: Long) {
        _startMillis.value = start
        // Mismo día de inicio y fin = 1 día de alquiler.
        _endMillis.value = if (end > start) end else start + DAY_MS
    }

    fun clear() {
        _startMillis.value = null
        _endMillis.value = null
    }

    /** Días del rango elegido, o [default] si aún no se eligieron fechas. */
    fun days(default: Int = 2): Int {
        val s = _startMillis.value ?: return default
        val e = _endMillis.value ?: return default
        return ((e - s) / DAY_MS).toInt().coerceAtLeast(1)
    }
}