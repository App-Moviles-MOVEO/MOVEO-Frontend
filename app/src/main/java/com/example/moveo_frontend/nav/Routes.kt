package com.example.moveo_frontend.nav

object Routes {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT = "forgot"
    const val KYC = "kyc"
    const val MAIN = "main"

    const val CATALOG = "catalog"

    const val VEHICLE_DETAIL = "vehicle/{id}"
    fun vehicleDetail(id: String) = "vehicle/$id"

    const val PAYMENT = "payment/{id}"
    fun payment(id: String) = "payment/$id"

    const val PUBLISH_VEHICLE = "publish_vehicle"

    const val CARPOOL_SEARCH = "carpool_search"
    const val CARPOOL_PUBLISH = "carpool_publish"

    const val CARPOOL_DETAIL = "carpool/{id}"
    fun carpoolDetail(id: String) = "carpool/$id"

    const val RESERVATION_DETAIL = "reservation/{id}"
    fun reservationDetail(id: String) = "reservation/$id"

    const val TRIP_TRACKING = "tracking/{routeId}"
    fun tripTracking(routeId: String) = "tracking/$routeId"

    const val RATE = "rate/{targetUserId}?reservationId={reservationId}&routeId={routeId}"
    fun rate(targetUserId: String, reservationId: String? = null, routeId: String? = null): String {
        val r = reservationId?.let { "reservationId=$it" }.orEmpty()
        val ro = routeId?.let { "routeId=$it" }.orEmpty()
        val query = listOf(r, ro).filter { it.isNotBlank() }.joinToString("&")
        return if (query.isEmpty()) "rate/$targetUserId" else "rate/$targetUserId?$query"
    }

    const val CHAT = "chat/{peerId}"
    fun chat(peerId: String) = "chat/$peerId"

    const val NOTIFICATIONS = "notifications"
    const val PROFILE = "profile"
    const val REWARDS = "rewards"
    const val SAFETY = "safety"
    const val RESERVATIONS = "reservations"
}
