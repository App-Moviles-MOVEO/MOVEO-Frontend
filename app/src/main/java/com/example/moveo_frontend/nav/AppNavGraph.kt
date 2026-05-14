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
    NavHost(navController = nav, startDestination = Routes.WELCOME) {
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
                    nav.navigate(Routes.MAIN) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                },
                onRegister = { nav.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onBack = { nav.popBackStack() },
                onContinue = { nav.navigate(Routes.KYC) }
            )
        }
        composable(Routes.KYC) {
            KycScreen(onFinish = {
                nav.navigate(Routes.MAIN) {
                    popUpTo(Routes.WELCOME) { inclusive = true }
                }
            })
        }
        composable(Routes.MAIN) {
            MainScreen(
                onCatalog = { nav.navigate(Routes.CATALOG) },
                onCarpoolSearch = { nav.navigate(Routes.CARPOOL_SEARCH) },
                onSafety = { nav.navigate(Routes.SAFETY) },
                onRewards = { nav.navigate(Routes.REWARDS) },
                onVehicleClick = { id -> nav.navigate(Routes.vehicleDetail(id)) }
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
                onReserve = { nav.navigate(Routes.payment(it)) }
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
                    nav.navigate(Routes.MAIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.CARPOOL_SEARCH) {
            CarpoolSearchScreen(
                onBack = { nav.popBackStack() },
                onPublish = { nav.navigate(Routes.CARPOOL_PUBLISH) }
            )
        }
        composable(Routes.CARPOOL_PUBLISH) {
            CarpoolPublishScreen(
                onBack = { nav.popBackStack() },
                onPublished = { nav.popBackStack() }
            )
        }
        composable(Routes.SAFETY) {
            SafetyScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.REWARDS) {
            RewardsScreen(onBack = { nav.popBackStack() })
        }
    }
}
