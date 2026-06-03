package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.ui.components.StateContainer
import com.example.moveo_frontend.ui.components.WPButton
import com.example.moveo_frontend.ui.components.WPOutlinedButton
import com.example.moveo_frontend.ui.viewmodel.CarpoolViewModel
import com.example.moveo_frontend.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarpoolConfirmScreen(id: String, onBack: () -> Unit, onDone: () -> Unit) {
    val vm: CarpoolViewModel = viewModel()
    val state by vm.detail.collectAsState()
    val bookState by vm.book.collectAsState()
    LaunchedEffect(id) { vm.loadDetail(id) }

    var seats by remember { mutableStateOf(1) }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Confirmar reserva") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
        })
    }) { padding ->
        Box(Modifier.padding(padding)) {
            if (bookState is UiState.Success) {
                Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(96.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("¡Asiento reservado!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("El conductor recibirá tu solicitud. Coordina los detalles por el chat.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(32.dp))
                    WPButton("Listo", onClick = onDone)
                }
            } else {
                StateContainer(state, onRetry = { vm.loadDetail(id) }) { r ->
                    val maxSeats = r.seatsAvailable.coerceAtLeast(1)
                    if (seats > maxSeats) seats = maxSeats
                    val total = r.pricePerSeat * seats

                    Column(Modifier.fillMaxSize()) {
                        Column(
                            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp)
                        ) {
                            Text("Resumen del viaje", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(10.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp),
                                tonalElevation = 1.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(Modifier.padding(14.dp)) {
                                    SummaryLine("Conductor", r.driverName)
                                    SummaryLine("Ruta", "${r.origin} → ${r.destination}")
                                    SummaryLine("Salida", "${r.departureTime} · ${r.date}")
                                    SummaryLine("Vehículo", r.vehicleModel)
                                }
                            }

                            Spacer(Modifier.height(20.dp))
                            Text("¿Cuántos asientos?", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp),
                                tonalElevation = 1.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text("Asientos", fontWeight = FontWeight.SemiBold)
                                        Text("$maxSeats disponibles", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    OutlinedIconButton(onClick = { if (seats > 1) seats-- }, enabled = seats > 1) {
                                        Icon(Icons.Default.Remove, "Quitar")
                                    }
                                    Text("$seats", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 12.dp))
                                    OutlinedIconButton(onClick = { if (seats < maxSeats) seats++ }, enabled = seats < maxSeats) {
                                        Icon(Icons.Default.Add, "Agregar")
                                    }
                                }
                            }

                            Spacer(Modifier.height(20.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text("Total a pagar", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("$seats x S/ ${r.pricePerSeat}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text("S/ $total", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }

                            if (bookState is UiState.Error) {
                                Spacer(Modifier.height(12.dp))
                                Text((bookState as UiState.Error).message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                            }
                        }

                        Surface(tonalElevation = 4.dp) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                WPButton(
                                    text = if (bookState is UiState.Loading) "Procesando..." else "Pagar con Yape S/ $total",
                                    enabled = bookState !is UiState.Loading,
                                    onClick = { vm.book(r, seats) }
                                )
                                WPOutlinedButton(
                                    text = "Confirmar y pagar al subir",
                                    onClick = { vm.book(r, seats) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.weight(1f))
        Text(value, fontWeight = FontWeight.SemiBold, color = Color.Unspecified)
    }
}
