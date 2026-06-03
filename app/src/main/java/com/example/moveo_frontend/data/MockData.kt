package com.example.moveo_frontend.data

import com.example.moveo_frontend.data.remote.dto.ChatMessageDto
import com.example.moveo_frontend.data.remote.dto.NotificationDto
import com.example.moveo_frontend.data.remote.dto.PaymentMethodDto
import com.example.moveo_frontend.data.remote.dto.TrackingPointDto

object MockData {
    val currentUser = User(
        name = "Esther Ospina",
        email = "u202318049@upc.edu.pe",
        role = UserRole.PASSENGER,
        rating = 4.9,
        tripsCompleted = 47,
        verified = true,
        badges = listOf("KYC", "UPC", "Plata"),
        rewardPoints = 450
    )

    val vehicles = listOf(
        Vehicle("v1", "Hyundai", "i10", 2022, 85, 4.9, "Rosa M.", true, "Surco, Lima", "Compacto", "Mecánico", 5, "Gasolina", "Compacto ideal para la ciudad. Bajo consumo, recién pasado revisión técnica.", "🚗"),
        Vehicle("v2", "Toyota", "Yaris", 2023, 110, 4.8, "Carlos P.", true, "Magdalena, Lima", "Sedán", "Automático", 5, "Gasolina", "Toyota Yaris automático, perfecto para viajes ejecutivos.", "🚙"),
        Vehicle("v3", "Kia", "Sportage", 2024, 180, 5.0, "Alex A.", true, "Miraflores, Lima", "SUV", "Automático", 7, "Gasolina", "SUV familiar, espaciosa y cómoda para viajes largos.", "🚐"),
        Vehicle("v4", "Chevrolet", "Spark", 2021, 70, 4.6, "Ana M.", true, "Surquillo, Lima", "Compacto", "Mecánico", 4, "Gasolina", "Económico y ágil. Ideal para uso urbano diario.", "🚗"),
        Vehicle("v5", "Mazda", "CX-5", 2023, 200, 4.9, "David G.", true, "San Isidro, Lima", "SUV", "Automático", 5, "Gasolina", "SUV premium con todas las comodidades.", "🚙")
    )

    // Publicaciones del propio usuario (lo que YO ofrezco).
    val myVehicles = listOf(
        Vehicle("mv1", "Suzuki", "Swift", 2022, 95, 4.8, "Tú", true, "Surco, Lima", "Compacto", "Mecánico", 5, "Gasolina", "Mi Swift, siempre limpio y mantenido al día.", "🚗"),
        Vehicle("mv2", "Nissan", "Versa", 2023, 120, 5.0, "Tú", true, "Surco, Lima", "Sedán", "Automático", 5, "Gasolina", "Versa automático, cómodo y con bajo consumo.", "🚙")
    )

    val myRoutes = listOf(
        CarpoolRoute("mr1", "Tú", 4.9, true, "Surco", "UPC Monterrico", "07:00", "L-V", 3, 8, "Suzuki Swift", "UPC")
    )

    val routes = listOf(
        CarpoolRoute("r1", "Diego A.", 4.9, true, "Los Olivos", "UPC Villa", "07:15", "Vie 9 may", 2, 8, "Toyota Yaris", "UPC"),
        CarpoolRoute("r2", "María L.", 5.0, true, "San Juan de Lurigancho", "San Isidro", "06:30", "L-V", 3, 10, "Kia Picanto", "UPC", onlyWomen = true),
        CarpoolRoute("r3", "Javier Q.", 4.8, true, "San Miguel", "UPC Monterrico", "07:00", "L-V", 1, 7, "Hyundai Accent", "UPC"),
        CarpoolRoute("r4", "Paul E.", 4.7, true, "Miraflores", "San Isidro (Centro Empresarial)", "08:00", "L-V", 2, 6, "Toyota Corolla", "Empresa")
    )

    val reviews = listOf(
        Review("Diego A.", 5, "Excelente compañera de viaje, muy puntual.", "Hace 2 días"),
        Review("María L.", 5, "Súper amable y conversadora. ¡Recomendada!", "Hace 1 semana"),
        Review("Paul E.", 4, "Buena experiencia, todo en orden.", "Hace 2 semanas")
    )

    val reservations = listOf(
        Reservation("res1", "Hyundai i10", "9 May", "11 May", 255, "Confirmado"),
        Reservation("res2", "Toyota Yaris", "15 May", "16 May", 110, "En curso"),
        Reservation("res3", "Kia Sportage", "1 May", "3 May", 540, "Finalizado")
    )

    val notifications = listOf(
        NotificationDto("n1", "Reserva confirmada", "Tu Hyundai i10 está listo para el 9 de mayo.", "hace 5 min", false),
        NotificationDto("n2", "Nuevo mensaje de Diego A.", "Nos vemos a las 7:15 en el paradero.", "hace 1 h", false),
        NotificationDto("n3", "Verificación KYC aprobada", "Ya eres un usuario verificado.", "ayer", true),
        NotificationDto("n4", "Ganaste 50 pts", "Por completar tu primer alquiler.", "hace 2 d", true)
    )

    val paymentMethods = listOf(
        PaymentMethodDto("pm1", "Pago instantáneo", "Yape"),
        PaymentMethodDto("pm2", "Pago instantáneo", "Plin"),
        PaymentMethodDto("pm3", "Tarjeta de crédito", "Visa **** 4521")
    )

    fun chatWith(peerId: String) = listOf(
        ChatMessageDto("c1", peerId, "¡Hola! Acabo de confirmar el viaje.", "10:02", false),
        ChatMessageDto("c2", "me", "Perfecto, te veo en el punto de encuentro.", "10:03", true),
        ChatMessageDto("c3", peerId, "Voy llegando en 5 min.", "10:08", false),
        ChatMessageDto("c4", "me", "Listo, ya estoy aquí.", "10:09", true)
    )

    val trackingPoints = listOf(
        TrackingPointDto(-12.0464, -77.0428, "10:00"),
        TrackingPointDto(-12.0500, -77.0470, "10:05"),
        TrackingPointDto(-12.0550, -77.0500, "10:10"),
        TrackingPointDto(-12.0600, -77.0550, "10:15"),
        TrackingPointDto(-12.0650, -77.0600, "10:20")
    )
}
