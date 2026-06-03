package com.example.moveo_frontend.data.repository

import com.example.moveo_frontend.BuildConfig
import com.example.moveo_frontend.data.CarpoolRoute
import com.example.moveo_frontend.data.MockData
import com.example.moveo_frontend.data.remote.api.CarpoolingApi
import com.example.moveo_frontend.data.remote.dto.BookSeatRequest
import com.example.moveo_frontend.data.remote.dto.PublishRouteRequest
import com.example.moveo_frontend.data.remote.dto.TrackingPointDto
import kotlinx.coroutines.delay

class CarpoolingRepository(private val api: CarpoolingApi) {
    suspend fun routes(onlyWomen: Boolean? = null, verified: Boolean? = null): Result<List<CarpoolRoute>> = runCatching {
        if ((BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_CARPOOLING)) {
            delay(400)
            return@runCatching MockData.routes.filter {
                (onlyWomen != true || it.onlyWomen) && (verified != true || it.verified)
            }
        }
        api.list(onlyWomen, verified).map { it.toDomain() }
    }
    suspend fun route(id: String): Result<CarpoolRoute> = runCatching {
        if ((BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_CARPOOLING)) {
            delay(300)
            return@runCatching MockData.routes.firstOrNull { it.id == id }
                ?: error("Ruta no encontrada")
        }
        api.detail(id).toDomain()
    }
    suspend fun publish(req: PublishRouteRequest): Result<CarpoolRoute> = runCatching {
        if ((BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_CARPOOLING)) {
            delay(600)
            return@runCatching CarpoolRoute(
                id = "r_new_${System.currentTimeMillis()}",
                driverName = "Tú", driverRating = 5.0, verified = true,
                origin = req.origin, destination = req.destination,
                departureTime = req.time, date = req.date,
                seatsAvailable = req.seats, pricePerSeat = req.price,
                vehicleModel = "Mi vehículo", community = "UPC"
            )
        }
        api.publish(req).toDomain()
    }
    suspend fun book(routeId: String, seats: Int): Result<Unit> = runCatching {
        if ((BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_CARPOOLING)) { delay(500); return@runCatching }
        api.book(BookSeatRequest(routeId, seats))
    }
    suspend fun tracking(routeId: String): Result<List<TrackingPointDto>> = runCatching {
        if ((BuildConfig.USE_MOCK_DATA || BuildConfig.USE_MOCK_CARPOOLING)) {
            delay(400)
            return@runCatching MockData.trackingPoints
        }
        api.tracking(routeId)
    }
}
