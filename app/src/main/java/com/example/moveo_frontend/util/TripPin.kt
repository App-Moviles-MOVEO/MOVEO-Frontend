package com.example.moveo_frontend.util

/**
 * PIN de 4 dígitos para validar la entrega/inicio del viaje (US09).
 *
 * El backend aún no expone un PIN por alquiler, así que se deriva de forma
 * **determinística** del id del alquiler. La app Owner (Flutter) usa exactamente
 * esta misma fórmula (`lib/core/utils/trip_pin.dart`), de modo que el
 * arrendatario ve el PIN en su app y el proveedor lo valida en la suya sin
 * necesidad de un endpoint.
 *
 * Cuando el backend agregue un PIN real por reserva, se reemplaza esta
 * derivación por el valor del servidor sin tocar la UI.
 */
object TripPin {
    /** PIN de 4 dígitos (0000–9999) estable para un mismo [rentalId]. */
    fun forRental(rentalId: String): String {
        // Hash tipo FNV-1a de 32 bits sobre "wpe-trip:{id}" (mismo que el Owner),
        // luego se lleva a 4 dígitos. Se usa Long y máscara de 32 bits para
        // reproducir el desbordamiento sin signo de Dart.
        val seed = "wpe-trip:"
        var hash = 0x811c9dc5L
        for (ch in "$seed$rentalId") {
            hash = hash xor ch.code.toLong()
            hash = (hash * 0x01000193L) and 0xFFFFFFFFL
        }
        val pin = (hash % 10000L).toInt()
        return pin.toString().padStart(4, '0')
    }
}
