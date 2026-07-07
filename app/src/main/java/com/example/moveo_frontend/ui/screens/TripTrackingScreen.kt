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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.R
import com.example.moveo_frontend.ui.components.WPBackButton
import com.example.moveo_frontend.ui.components.WPButton
import com.example.moveo_frontend.ui.viewmodel.TrackingViewModel
import com.example.moveo_frontend.ui.viewmodel.UiState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
    val current by vm.current.collectAsState()
    val progress by vm.progress.collectAsState()
    val etaMinutes by vm.etaMinutes.collectAsState()
    val completeState by vm.completeState.collectAsState()
    val sosState by vm.sosState.collectAsState()
    val started by vm.started.collectAsState()
    var showArrivalDialog by remember { mutableStateOf(false) }
    var showSosDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val carIcon = remember { carMarkerDescriptor(context) }

    LaunchedEffect(routeId) { vm.load(routeId) }

    // US08: feedback de la alerta de emergencia (toast + reset).
    LaunchedEffect(sosState) {
        when (val s = sosState) {
            is UiState.Success -> {
                Toast.makeText(
                    context,
                    "Alerta de emergencia enviada. Soporte fue notificado.",
                    Toast.LENGTH_LONG
                ).show()
                vm.resetSos()
            }
            is UiState.Error -> {
                Toast.makeText(context, s.message, Toast.LENGTH_LONG).show()
                vm.resetSos()
            }
            else -> {}
        }
    }

    val fallback = listOf(
        LatLng(-12.0464, -77.0428),
        LatLng(-12.0510, -77.0500),
        LatLng(-12.0600, -77.0560)
    )
    val path = points.map { LatLng(it.lat, it.lng) }.ifEmpty { fallback }
    val livePosition = current?.let { LatLng(it.lat, it.lng) } ?: path.first()
    val arrived = progress >= 1f
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(path.first(), 14f)
    }

    // US06: la cámara sigue en tiempo real la posición del vehículo.
    LaunchedEffect(livePosition) {
        cameraPositionState.animate(CameraUpdateFactory.newLatLng(livePosition), 900)
    }

    // US20: llegada confirmada en backend → pasar a calificar el viaje.
    LaunchedEffect(completeState) {
        if (completeState is UiState.Success) {
            vm.resetComplete()
            onRate()
        }
    }

    if (showArrivalDialog) {
        AlertDialog(
            onDismissRequest = { showArrivalDialog = false },
            title = { Text("¿Llegaste a tu destino?") },
            text = { Text("Se confirmará la llegada y el viaje pasará a Finalizado. Después podrás calificarlo.") },
            confirmButton = {
                TextButton(onClick = {
                    showArrivalDialog = false
                    vm.confirmArrival(routeId)
                }) { Text("Sí, he llegado") }
            },
            dismissButton = { TextButton(onClick = { showArrivalDialog = false }) { Text("Aún no") } }
        )
    }
    if (showSosDialog) {
        AlertDialog(
            onDismissRequest = { showSosDialog = false },
            title = { Text("¿Activar alerta de emergencia?") },
            text = {
                Text(
                    "Se enviará una alerta con tu ubicación al equipo de soporte y " +
                        "quedará registrada. Úsala solo ante una situación real."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showSosDialog = false
                    vm.triggerSos(routeId)
                }) { Text("Enviar alerta") }
            },
            dismissButton = {
                TextButton(onClick = { showSosDialog = false }) { Text("Cancelar") }
            }
        )
    }
    if (completeState is UiState.Error) {
        AlertDialog(
            onDismissRequest = { vm.resetComplete() },
            title = { Text("No se pudo confirmar la llegada") },
            text = { Text((completeState as UiState.Error).message) },
            confirmButton = {
                TextButton(onClick = { vm.confirmArrival(routeId) }) { Text("Reintentar") }
            },
            dismissButton = {
                TextButton(onClick = { vm.resetComplete(); onRate() }) { Text("Calificar igual") }
            }
        )
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
                Marker(
                    state = MarkerState(position = livePosition),
                    title = "Tu vehículo",
                    icon = carIcon,
                    anchor = Offset(0.5f, 0.5f),
                    zIndex = 2f
                )
                Polyline(points = path, color = MaterialTheme.colorScheme.primary, width = 10f)
            }
            Surface(tonalElevation = 4.dp) {
                Column(Modifier.padding(16.dp)) {
                    // US06: estado del monitoreo en tiempo real (avance y ETA).
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        when {
                            arrived -> "Has llegado a tu destino 🎉"
                            !started -> "Listo para iniciar. Pulsa \"Iniciar viaje\" para comenzar."
                            etaMinutes != null -> "En ruta · llegada estimada en $etaMinutes min"
                            else -> "Conectando con el GPS..."
                        },
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(12.dp))
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
                            onClick = { if (sosState !is UiState.Loading) showSosDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                if (sosState is UiState.Loading) "Enviando..." else "SOS",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    if (!started) {
                        // El viaje aún no arranca: el pasajero confirma el inicio y el auto empieza a moverse.
                        WPButton(
                            text = "Iniciar viaje",
                            onClick = { vm.startTrip() }
                        )
                    } else {
                        WPButton(
                            if (completeState is UiState.Loading) "Confirmando llegada..."
                            else "He llegado · Confirmar y calificar",
                            onClick = { if (completeState !is UiState.Loading) showArrivalDialog = true }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Convierte el drawable vectorial del auto (ic_car_marker) en un BitmapDescriptor
 * para usarlo como ícono del marcador "Tu vehículo" en el mapa.
 */
private fun carMarkerDescriptor(context: android.content.Context): BitmapDescriptor {
    val drawable = ContextCompat.getDrawable(context, R.drawable.ic_car_marker)!!
    val w = drawable.intrinsicWidth.coerceAtLeast(1)
    val h = drawable.intrinsicHeight.coerceAtLeast(1)
    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, w, h)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
