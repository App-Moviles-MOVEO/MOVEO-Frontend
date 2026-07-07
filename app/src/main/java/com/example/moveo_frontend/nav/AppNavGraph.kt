package com.example.moveo_frontend.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moveo_frontend.di.ServiceLocator
import com.example.moveo_frontend.ui.screens.*
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(startDestination: String = Routes.WELCOME) {
    val nav = rememberNavController()
    val scope = rememberCoroutineScope()
    NavHost(navController = nav, startDestination = startDestination) {
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
                onContinue = { nav.navigate(Routes.KYC) },
                onTerms = { nav.navigate(Routes.TERMS) }
            )
        }
        composable(Routes.KYC) {
            KycScreen(
                onBack = {
                    nav.navigate(Routes.WELCOME) { popUpTo(0) { inclusive = true } }
                },
                onFinish = {
                    scope.launch { ServiceLocator.session.setKycCompleted() }
                    nav.navigate(Routes.MAIN) { popUpTo(0) { inclusive = true } }
                }
            )
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
                onPublishVehicle = { nav.navigate(Routes.PUBLISH_VEHICLE) },
                onPublishCarpool = { nav.navigate(Routes.CARPOOL_PUBLISH) },
                onEditProfile = { nav.navigate(Routes.PROFILE_EDIT) },
                onPaymentMethods = { nav.navigate(Routes.PAYMENT_METHODS) },
                onMyListings = { nav.navigate(Routes.MY_LISTINGS) },
                onSettings = { nav.navigate(Routes.SETTINGS) },
                onHelp = { nav.navigate(Routes.HELP) },
                onLogout = {
                    nav.navigate(Routes.WELCOME) { popUpTo(Routes.MAIN) { inclusive = true } }
                }
            )
        }
        composable(Routes.PROFILE_EDIT) {
            EditProfileScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.PAYMENT_METHODS) {
            PaymentMethodsScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.MY_LISTINGS) {
            MyListingsScreen(
                onBack = { nav.popBackStack() },
                onPublishVehicle = { nav.navigate(Routes.PUBLISH_VEHICLE) },
                onPublishCarpool = { nav.navigate(Routes.CARPOOL_PUBLISH) }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { nav.popBackStack() },
                onHelp = { nav.navigate(Routes.HELP) },
                onTerms = { nav.navigate(Routes.TERMS) },
                onLoggedOut = {
                    nav.navigate(Routes.WELCOME) { popUpTo(0) { inclusive = true } }
                }
            )
        }
        composable(Routes.HELP) {
            HelpScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.TERMS) {
            TermsScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.PUBLISH_VEHICLE) {
            PublishVehicleScreen(
                onBack = { nav.popBackStack() },
                onPublished = { nav.popBackStack() }
            )
        }
        composable(Routes.CARPOOL_PUBLISH) {
            CarpoolPublishScreen(
                onBack = { nav.popBackStack() },
                onPublished = { nav.popBackStack() }
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
                onReserve = { routeId -> nav.navigate(Routes.carpoolConfirm(routeId)) },
                onChat = { peer -> nav.navigate(Routes.chat(peer)) }
            )
        }
        composable(
            Routes.CARPOOL_CONFIRM,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("id").orEmpty()
            CarpoolConfirmScreen(
                id = id,
                onBack = { nav.popBackStack() },
                onTrack = { nav.navigate(Routes.tripTracking(id)) },
                onDone = { nav.navigate(Routes.MAIN) { popUpTo(Routes.MAIN) { inclusive = true } } }
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
