package com.example.moveo_frontend.ui.screens

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
    onSafety: () -> Unit,
    onRewards: () -> Unit,
    onVehicleClick: (String) -> Unit
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
                label = { Text("Reservas") }
            )
            NavigationBarItem(
                selected = tab == 2,
                onClick = { tab = 2 },
                icon = { Icon(Icons.Default.Person, null) },
                label = { Text("Perfil") }
            )
        }
    }) { padding ->
        when (tab) {
            0 -> androidx.compose.foundation.layout.Box(Modifier.padding(padding)) {
                HomeScreen(onCatalog, onCarpoolSearch, onSafety, onRewards, onVehicleClick)
            }
            1 -> androidx.compose.foundation.layout.Box(Modifier.padding(padding)) {
                ReservationsScreen()
            }
            2 -> androidx.compose.foundation.layout.Box(Modifier.padding(padding)) {
                ProfileScreen()
            }
        }
    }
}
