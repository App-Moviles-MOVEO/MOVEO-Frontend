package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun MainScreen(
    onCatalog: () -> Unit,
    onCarpoolSearch: () -> Unit,
    onCarpoolDetail: (String) -> Unit,
    onSafety: () -> Unit,
    onRewards: () -> Unit,
    onNotifications: () -> Unit,
    onVehicleClick: (String) -> Unit,
    onReservationClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    var tab by remember { mutableStateOf(0) }
    Scaffold(bottomBar = {
        NavigationBar {
            NavigationBarItem(
                selected = tab == 0,
                onClick = { tab = 0 },
                icon = { Icon(Icons.Default.Home, null) },
                label = { Text("Inicio") }
            )
            NavigationBarItem(
                selected = tab == 1,
                onClick = { tab = 1 },
                icon = { Icon(Icons.Default.DirectionsCar, null) },
                label = { Text("Catálogo") }
            )
            NavigationBarItem(
                selected = tab == 2,
                onClick = { tab = 2 },
                icon = { Icon(Icons.Default.Route, null) },
                label = { Text("Carpool") }
            )
            NavigationBarItem(
                selected = tab == 3,
                onClick = { tab = 3 },
                icon = { Icon(Icons.Default.EventNote, null) },
                label = { Text("Reservas") }
            )
            NavigationBarItem(
                selected = tab == 4,
                onClick = { tab = 4 },
                icon = { Icon(Icons.Default.Person, null) },
                label = { Text("Perfil") }
            )
        }
    }) { padding ->
        Box(Modifier.padding(padding)) {
            when (tab) {
                0 -> HomeScreen(onCatalog, onCarpoolSearch, onSafety, onRewards, onNotifications, onVehicleClick)
                1 -> CatalogScreen(onBack = { tab = 0 }, onVehicleClick = onVehicleClick)
                2 -> CarpoolSearchScreen(
                    onBack = { tab = 0 },
                    onRouteClick = onCarpoolDetail
                )
                3 -> ReservationsScreen(onClick = onReservationClick)
                4 -> ProfileScreen(onLogout = onLogout)
            }
        }
    }
}
