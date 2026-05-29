package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Schedule
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
import com.example.moveo_frontend.ui.components.RatingChip
import com.example.moveo_frontend.ui.components.StateContainer
import com.example.moveo_frontend.ui.components.VerifiedBadge
import com.example.moveo_frontend.ui.components.WPButton
import com.example.moveo_frontend.ui.viewmodel.CarpoolViewModel
import com.example.moveo_frontend.ui.viewmodel.UiState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarpoolDetailScreen(id: String, onBack: () -> Unit, onBooked: () -> Unit, onChat: (String) -> Unit) {
    val vm: CarpoolViewModel = viewModel()
    val state by vm.detail.collectAsState()
    val bookState by vm.book.collectAsState()
    LaunchedEffect(id) { vm.loadDetail(id) }
    LaunchedEffect(bookState) { if (bookState is UiState.Success) onBooked() }

    val lima = LatLng(-12.0464, -77.0428)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(lima, 12f)
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Detalle de ruta") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
        }, actions = {
            IconButton(onClick = { onChat(id) }) { Icon(Icons.Filled.Chat, null) }
        })
    }) { padding ->
        Box(Modifier.padding(padding)) {
            StateContainer(state, onRetry = { vm.loadDetail(id) }) { r ->
                val scroll = rememberScrollState()
                Column {
                    Column(Modifier.weight(1f).verticalScroll(scroll)) {
                        Box(Modifier.fillMaxWidth().height(220.dp)) {
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState
                            ) {
                                Marker(state = MarkerState(position = lima), title = r.origin)
                                Marker(state = MarkerState(position = LatLng(lima.latitude - 0.05, lima.longitude + 0.05)), title = r.destination)
                                Polyline(points = listOf(lima, LatLng(lima.latitude - 0.05, lima.longitude + 0.05)), color = MaterialTheme.colorScheme.primary, width = 8f)
                            }
                        }
                        Column(Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(56.dp).background(MaterialTheme.colorScheme.primary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) { Text(r.driverName.first().toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp) }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(r.driverName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        Spacer(Modifier.width(6.dp))
                                        if (r.verified) VerifiedBadge(r.community)
                                    }
                                    Text(r.vehicleModel, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                                }
                                RatingChip(r.driverRating)
                            }
                            Spacer(Modifier.height(18.dp))
                            InfoLine(Icons.Default.MyLocation, r.origin, MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(8.dp))
                            InfoLine(Icons.Default.LocationOn, r.destination, MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(8.dp))
                            InfoLine(Icons.Default.Schedule, "${r.departureTime} · ${r.date}")
                            Spacer(Modifier.height(8.dp))
                            InfoLine(Icons.Default.AirlineSeatReclineNormal, "${r.seatsAvailable} cupos disponibles")
                            Spacer(Modifier.height(20.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(Modifier.padding(14.dp)) {
                                    Text("Precio por asiento", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("S/ ${r.pricePerSeat}", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            if (bookState is UiState.Error) {
                                Spacer(Modifier.height(12.dp))
                                Text((bookState as UiState.Error).message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                            }
                        }
                    }
                    Surface(tonalElevation = 4.dp) {
                        Row(Modifier.padding(16.dp)) {
                            WPButton(
                                text = if (bookState is UiState.Loading) "Reservando..." else "Reservar asiento",
                                enabled = bookState !is UiState.Loading,
                                onClick = { vm.book(r.id, 1) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoLine(icon: ImageVector, text: String, tint: Color = MaterialTheme.colorScheme.onSurface) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(18.dp), tint = tint)
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 14.sp)
    }
}
