package com.example.moveo_frontend.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moveo_frontend.ui.screens.*

@Composable
fun AppNavGraph() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onAuthed = {
                    nav.navigate(Routes.MAIN) { popUpTo(Routes.SPLASH) { inclusive = true } }
                },
                onUnauthed = {
                    nav.navigate(Routes.WELCOME) { popUpTo(Routes.SPLASH) { inclusive = true } }
                }
            )
        }
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onLogin = { nav.navigate(Routes.LOGIN) },
                onRegister = { nav.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                onBack = { nav.popBackStack() },
                onLogin = {
                    nav.navigate(Routes.MAIN) { popUpTo(Routes.WELCOME) { inclusive = true } }
                },
                onRegister = { nav.navigate(Routes.REGISTER) },
                onForgot = { nav.navigate(Routes.FORGOT) }
            )
        }
        composable(Routes.FORGOT) {
            ForgotPasswordScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onBack = { nav.popBackStack() },
                onContinue = { nav.navigate(Routes.KYC) }
            )
        }
        composable(Routes.KYC) {
            KycScreen(onFinish = {
                nav.navigate(Routes.MAIN) { popUpTo(Routes.WELCOME) { inclusive = true } }
            })
        }
        composable(Routes.MAIN) {
            MainScreen(
                onCatalog = { nav.navigate(Routes.CATALOG) },
                onCarpoolSearch = { nav.navigate(Routes.CARPOOL_SEARCH) },
                onCarpoolDetail = { id -> nav.navigate(Routes.carpoolDetail(id)) },
                onSafety = { nav.navigate(Routes.SAFETY) },
                onRewards = { nav.navigate(Routes.REWARDS) },
                onNotifications = { nav.navigate(Routes.NOTIFICATIONS) },
                onVehicleClick = { id -> nav.navigate(Routes.vehicleDetail(id)) },
                onReservationClick = { id -> nav.navigate(Routes.reservationDetail(id)) },
                onLogout = {
                    nav.navigate(Routes.WELCOME) { popUpTo(Routes.MAIN) { inclusive = true } }
                }
            )
        }
        composable(Routes.CATALOG) {
            CatalogScreen(
                onBack = { nav.popBackStack() },
                onVehicleClick = { id -> nav.navigate(Routes.vehicleDetail(id)) }
            )
        }
        composable(
            Routes.VEHICLE_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("id").orEmpty()
            VehicleDetailScreen(
                id = id,
                onBack = { nav.popBackStack() },
                onReserve = { nav.navigate(Routes.payment(it)) },
                onChat = { peer -> nav.navigate(Routes.chat(peer)) }
            )
        }
        composable(
            Routes.PAYMENT,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("id").orEmpty()
            PaymentScreen(
                id = id,
                onBack = { nav.popBackStack() },
                onSuccess = {
                    nav.navigate(Routes.MAIN) { popUpTo(Routes.MAIN) { inclusive = true } }
                }
            )
        }
        composable(Routes.CARPOOL_SEARCH) {
            CarpoolSearchScreen(
                onBack = { nav.popBackStack() },
                onRouteClick = { id -> nav.navigate(Routes.carpoolDetail(id)) }
            )
        }
        composable(
            Routes.CARPOOL_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("id").orEmpty()
            CarpoolDetailScreen(
                id = id,
                onBack = { nav.popBackStack() },
                onBooked = { nav.popBackStack() },
                onChat = { peer -> nav.navigate(Routes.chat(peer)) }
            )
        }
        composable(
            Routes.RESERVATION_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("id").orEmpty()
            ReservationDetailScreen(
                id = id,
                onBack = { nav.popBackStack() },
                onTrack = { nav.navigate(Routes.tripTracking(id)) },
                onRate = { nav.navigate(Routes.rate(targetUserId = id, reservationId = id)) }
            )
        }
        composable(
            Routes.TRIP_TRACKING,
            arguments = listOf(navArgument("routeId") { type = NavType.StringType })
        ) { entry ->
            val rid = entry.arguments?.getString("routeId").orEmpty()
            TripTrackingScreen(
                routeId = rid,
                onBack = { nav.popBackStack() },
                onRate = { nav.navigate(Routes.rate(targetUserId = rid, routeId = rid)) }
            )
        }
        composable(
            Routes.RATE,
            arguments = listOf(
                navArgument("targetUserId") { type = NavType.StringType },
                navArgument("reservationId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("routeId") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { entry ->
            val target = entry.arguments?.getString("targetUserId").orEmpty()
            val res = entry.arguments?.getString("reservationId")
            val route = entry.arguments?.getString("routeId")
            RateTripScreen(
                targetUserId = target,
                reservationId = res,
                routeId = route,
                onBack = { nav.popBackStack() },
                onDone = { nav.popBackStack() }
            )
        }
        composable(
            Routes.CHAT,
            arguments = listOf(navArgument("peerId") { type = NavType.StringType })
        ) { entry ->
            val peer = entry.arguments?.getString("peerId").orEmpty()
            ChatScreen(peerId = peer, onBack = { nav.popBackStack() })
        }
        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.SAFETY) {
            SafetyScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.REWARDS) {
            RewardsScreen(onBack = { nav.popBackStack() })
        }
    }
}
