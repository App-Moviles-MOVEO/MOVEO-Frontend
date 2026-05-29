package com.example.moveo_frontend.data.repository

import com.example.moveo_frontend.BuildConfig
import com.example.moveo_frontend.data.MockData
import com.example.moveo_frontend.data.Reservation
import com.example.moveo_frontend.data.Vehicle
import com.example.moveo_frontend.data.remote.api.RentalApi
import com.example.moveo_frontend.data.remote.dto.CreateReservationRequest
import com.example.moveo_frontend.data.remote.dto.PublishVehicleRequest
import kotlinx.coroutines.delay

class RentalRepository(private val api: RentalApi) {
    suspend fun vehicles(type: String? = null, query: String? = null): Result<List<Vehicle>> = runCatching {
        if (BuildConfig.USE_MOCK_DATA) {
            delay(400)
            return@runCatching if (type == null) MockData.vehicles else MockData.vehicles.filter { it.type == type }
        }
        api.list(type, query).map { it.toDomain() }
    }
    suspend fun vehicle(id: String): Result<Vehicle> = runCatching {
        if (BuildConfig.USE_MOCK_DATA) {
            delay(300)
            return@runCatching MockData.vehicles.firstOrNull { it.id == id }
                ?: error("Vehículo no encontrado")
        }
        api.detail(id).toDomain()
    }
    suspend fun publish(req: PublishVehicleRequest): Result<Vehicle> = runCatching {
        if (BuildConfig.USE_MOCK_DATA) {
            delay(700)
            return@runCatching Vehicle(
                id = "v_new_${System.currentTimeMillis()}",
                brand = req.brand, model = req.model, year = req.year,
                pricePerDay = req.pricePerDay, rating = 5.0, ownerName = "Tú",
                ownerVerified = true, location = req.location, type = req.type,
                transmission = req.transmission, seats = req.seats, fuel = req.fuel,
                description = req.description, imageEmoji = "🚗"
            )
        }
        api.publish(req).toDomain()
    }
    suspend fun reserve(vehicleId: String, startDate: String, endDate: String): Result<Reservation> = runCatching {
        if (BuildConfig.USE_MOCK_DATA) {
            delay(600)
            val v = MockData.vehicles.firstOrNull { it.id == vehicleId }
            return@runCatching Reservation(
                id = "res_${System.currentTimeMillis()}",
                vehicleName = v?.let { "${it.brand} ${it.model}" } ?: "Vehículo",
                startDate = startDate, endDate = endDate,
                total = (v?.pricePerDay ?: 0) * 3, status = "Confirmado"
            )
        }
        api.reserve(CreateReservationRequest(vehicleId, startDate, endDate)).toDomain()
    }
    suspend fun myReservations(): Result<List<Reservation>> = runCatching {
        if (BuildConfig.USE_MOCK_DATA) {
            delay(400)
            return@runCatching MockData.reservations
        }
        api.myReservations().map { it.toDomain() }
    }
    suspend fun reservation(id: String): Result<Reservation> = runCatching {
        if (BuildConfig.USE_MOCK_DATA) {
            delay(300)
            return@runCatching MockData.reservations.firstOrNull { it.id == id }
                ?: error("Reserva no encontrada")
        }
        api.reservation(id).toDomain()
    }
}
