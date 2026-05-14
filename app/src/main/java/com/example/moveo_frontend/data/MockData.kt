package com.example.moveo_frontend.data

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
        Vehicle(
            id = "v1",
            brand = "Hyundai",
            model = "i10",
            year = 2022,
            pricePerDay = 85,
            rating = 4.9,
            ownerName = "Rosa M.",
            ownerVerified = true,
            location = "Surco, Lima",
            type = "Compacto",
            transmission = "Mecánico",
            seats = 5,
            fuel = "Gasolina",
            description = "Compacto ideal para la ciudad. Bajo consumo, recién pasado revisión técnica.",
            imageEmoji = "🚗"
        ),
        Vehicle(
            id = "v2",
            brand = "Toyota",
            model = "Yaris",
            year = 2023,
            pricePerDay = 110,
            rating = 4.8,
            ownerName = "Carlos P.",
            ownerVerified = true,
            location = "Magdalena, Lima",
            type = "Sedán",
            transmission = "Automático",
            seats = 5,
            fuel = "Gasolina",
            description = "Toyota Yaris automático, perfecto para viajes ejecutivos.",
            imageEmoji = "🚙"
        ),
        Vehicle(
            id = "v3",
            brand = "Kia",
            model = "Sportage",
            year = 2024,
            pricePerDay = 180,
            rating = 5.0,
            ownerName = "Alex A.",
            ownerVerified = true,
            location = "Miraflores, Lima",
            type = "SUV",
            transmission = "Automático",
            seats = 7,
            fuel = "Gasolina",
            description = "SUV familiar, espaciosa y cómoda para viajes largos.",
            imageEmoji = "🚐"
        ),
        Vehicle(
            id = "v4",
            brand = "Chevrolet",
            model = "Spark",
            year = 2021,
            pricePerDay = 70,
            rating = 4.6,
            ownerName = "Ana M.",
            ownerVerified = true,
            location = "Surquillo, Lima",
            type = "Compacto",
            transmission = "Mecánico",
            seats = 4,
            fuel = "Gasolina",
            description = "Económico y ágil. Ideal para uso urbano diario.",
            imageEmoji = "🚗"
        ),
        Vehicle(
            id = "v5",
            brand = "Mazda",
            model = "CX-5",
            year = 2023,
            pricePerDay = 200,
            rating = 4.9,
            ownerName = "David G.",
            ownerVerified = true,
            location = "San Isidro, Lima",
            type = "SUV",
            transmission = "Automático",
            seats = 5,
            fuel = "Gasolina",
            description = "SUV premium con todas las comodidades.",
            imageEmoji = "🚙"
        )
    )

    val routes = listOf(
        CarpoolRoute(
            id = "r1",
            driverName = "Diego A.",
            driverRating = 4.9,
            verified = true,
            origin = "Los Olivos",
            destination = "UPC Villa",
            departureTime = "07:15",
            date = "Vie 9 may",
            seatsAvailable = 2,
            pricePerSeat = 8,
            vehicleModel = "Toyota Yaris",
            community = "UPC"
        ),
        CarpoolRoute(
            id = "r2",
            driverName = "María L.",
            driverRating = 5.0,
            verified = true,
            origin = "San Juan de Lurigancho",
            destination = "San Isidro",
            departureTime = "06:30",
            date = "L-V",
            seatsAvailable = 3,
            pricePerSeat = 10,
            vehicleModel = "Kia Picanto",
            community = "UPC",
            onlyWomen = true
        ),
        CarpoolRoute(
            id = "r3",
            driverName = "Javier Q.",
            driverRating = 4.8,
            verified = true,
            origin = "San Miguel",
            destination = "UPC Monterrico",
            departureTime = "07:00",
            date = "L-V",
            seatsAvailable = 1,
            pricePerSeat = 7,
            vehicleModel = "Hyundai Accent",
            community = "UPC"
        ),
        CarpoolRoute(
            id = "r4",
            driverName = "Paul E.",
            driverRating = 4.7,
            verified = true,
            origin = "Miraflores",
            destination = "San Isidro (Centro Empresarial)",
            departureTime = "08:00",
            date = "L-V",
            seatsAvailable = 2,
            pricePerSeat = 6,
            vehicleModel = "Toyota Corolla",
            community = "Empresa"
        )
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
}
