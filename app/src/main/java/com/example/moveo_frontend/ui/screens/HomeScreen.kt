package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.data.Vehicle
import com.example.moveo_frontend.ui.components.RatingChip
import com.example.moveo_frontend.ui.components.SectionTitle
import com.example.moveo_frontend.ui.theme.BluePrimary
import com.example.moveo_frontend.ui.viewmodel.ProfileViewModel
import com.example.moveo_frontend.ui.viewmodel.UiState
import com.example.moveo_frontend.ui.viewmodel.VehiclesViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun HomeScreen(
    onCatalog: () -> Unit,
    onCarpoolSearch: () -> Unit,
    onSafety: () -> Unit,
    onRewards: () -> Unit,
    onNotifications: () -> Unit,
    onVehicleClick: (String) -> Unit
) {
    val vehiclesVm: VehiclesViewModel = viewModel()
    val profileVm: ProfileViewModel = viewModel()
    val vehiclesState by vehiclesVm.state.collectAsState()
    val userState by profileVm.user.collectAsState()
    val scroll = rememberScrollState()

    val lima = LatLng(-12.0464, -77.0428)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(lima, 12f)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scroll)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        val userName = (userState as? UiState.Success)?.data?.name ?: "Usuario"
        val rewardPoints = (userState as? UiState.Success)?.data?.rewardPoints ?: 0
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .size(48.dp)
                    .background(BluePrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(userName.firstOrNull()?.toString().orEmpty(), color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Hola, ${userName.split(" ").first()} 👋", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("¿A dónde vas hoy?", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onNotifications) { Icon(Icons.Default.Notifications, null) }
        }
        Spacer(Modifier.height(16.dp))

        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(14.dp),
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth().clickable(onClick = onCatalog)
        ) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Buscar destino o vehículo", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(16.dp))

        // Mini-mapa
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth().height(180.dp)
        ) {
            val vehicles = (vehiclesState as? UiState.Success)?.data.orEmpty()
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(mapType = MapType.NORMAL),
                uiSettings = MapUiSettings(zoomControlsEnabled = false, mapToolbarEnabled = false)
            ) {
                Marker(state = MarkerState(position = lima), title = "Tú")
                vehicles.take(8).forEachIndexed { index, _ ->
                    val offset = 0.01 * (index + 1)
                    Marker(state = MarkerState(position = LatLng(lima.latitude + offset, lima.longitude - offset)), title = "Auto disponible")
                }
            }
        }
        Spacer(Modifier.height(20.dp))

        SectionTitle("Accesos rápidos")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickAction(Icons.Default.DirectionsCar, "Alquilar\nauto", Modifier.weight(1f), onCatalog)
            QuickAction(Icons.Default.Route, "Buscar\nruta", Modifier.weight(1f), onCarpoolSearch)
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickAction(Icons.Default.Shield, "Contactos de\nconfianza", Modifier.weight(1f), onSafety)
            QuickAction(Icons.Default.CardGiftcard, "Recompensas\n($rewardPoints pts)", Modifier.weight(1f), onRewards)
        }
        Spacer(Modifier.height(24.dp))

        SectionTitle("Cerca de ti")
        when (val s = vehiclesState) {
            is UiState.Loading -> CircularProgressIndicator(Modifier.padding(16.dp))
            is UiState.Error -> Text(s.message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            is UiState.Success -> {
                if (s.data.isEmpty()) {
                    Text("Sin vehículos disponibles", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(s.data) { v ->
                            NearbyCard(v) { onVehicleClick(v.id) }
                        }
                    }
                }
            }
            UiState.Idle -> Unit
        }
        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun QuickAction(icon: ImageVector, label: String, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 1.dp,
        modifier = modifier.height(96.dp).clickable(onClick = onClick)
    ) {
        Column(
            Modifier.padding(12.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun NearbyCard(v: Vehicle, onClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.width(180.dp).clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(12.dp)) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.DirectionsCar,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text("${v.brand} ${v.model}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("S/ ${v.pricePerDay}/día", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                Spacer(Modifier.weight(1f))
                RatingChip(v.rating)
            }
        }
    }
}
