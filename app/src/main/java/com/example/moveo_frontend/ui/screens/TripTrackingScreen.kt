package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.ui.components.WPBackButton
import com.example.moveo_frontend.ui.components.WPButton
import com.example.moveo_frontend.ui.viewmodel.TrackingViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripTrackingScreen(routeId: String, onBack: () -> Unit, onRate: () -> Unit) {
    val vm: TrackingViewModel = viewModel()
    val points by vm.points.collectAsState()

    LaunchedEffect(routeId) { vm.load(routeId) }

    val fallback = listOf(
        LatLng(-12.0464, -77.0428),
        LatLng(-12.0510, -77.0500),
        LatLng(-12.0600, -77.0560)
    )
    val path = points.map { LatLng(it.lat, it.lng) }.ifEmpty { fallback }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(path.first(), 14f)
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Viaje en curso") }, navigationIcon = {
            WPBackButton(onClick = onBack)
        })
    }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                cameraPositionState = cameraPositionState
            ) {
                Marker(state = MarkerState(position = path.first()), title = "Origen")
                Marker(state = MarkerState(position = path.last()), title = "Destino")
                Polyline(points = path, color = MaterialTheme.colorScheme.primary, width = 10f)
            }
            Surface(tonalElevation = 4.dp) {
                Column(Modifier.padding(16.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Compartiendo en vivo", fontWeight = FontWeight.SemiBold)
                                Text("Tus contactos de confianza ven tu ubicación", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { /* call driver */ }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Phone, null)
                            Spacer(Modifier.width(6.dp))
                            Text("Llamar")
                        }
                        Button(
                            onClick = { /* SOS */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("SOS", fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    WPButton("He llegado · Calificar viaje", onClick = onRate)
                }
            }
        }
    }
}

