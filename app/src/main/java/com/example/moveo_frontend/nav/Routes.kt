package com.example.moveo_frontend.nav

object Routes {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val KYC = "kyc"
    const val MAIN = "main" // hosts bottom nav
    const val HOME = "home"
    const val CATALOG = "catalog"
    const val VEHICLE_DETAIL = "vehicle/{id}"
    fun vehicleDetail(id: String) = "vehicle/$id"
    const val PAYMENT = "payment/{id}"
    fun payment(id: String) = "payment/$id"
    const val CARPOOL_SEARCH = "carpool_search"
    const val CARPOOL_PUBLISH = "carpool_publish"
    const val PROFILE = "profile"
    const val REWARDS = "rewards"
    const val SAFETY = "safety"
    const val RESERVATIONS = "reservations"
}
