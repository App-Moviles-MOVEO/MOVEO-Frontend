package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
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
import com.example.moveo_frontend.ui.components.StateContainer
import com.example.moveo_frontend.ui.components.WPButton
import com.example.moveo_frontend.ui.components.WPOutlinedButton
import com.example.moveo_frontend.ui.viewmodel.ReservationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailScreen(id: String, onBack: () -> Unit, onTrack: () -> Unit, onRate: () -> Unit) {
    val vm: ReservationsViewModel = viewModel()
    val state by vm.detail.collectAsState()
    LaunchedEffect(id) { vm.loadDetail(id) }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Detalle de reserva") }, navigationIcon = {
            WPBackButton(onClick = onBack)
        })
    }) { padding ->
        Box(Modifier.padding(padding)) {
            StateContainer(state, onRetry = { vm.loadDetail(id) }) { r ->
                Column(Modifier.padding(20.dp).fillMaxSize()) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(14.dp),
                        tonalElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(64.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) { Icon(Icons.Default.DirectionsCar, null, tint = MaterialTheme.colorScheme.primary) }
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(r.vehicleName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("${r.startDate} → ${r.endDate}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    r.status,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(14.dp),
                        tonalElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            DetailRow("ID reserva", r.id)
                            DetailRow("Inicio", r.startDate)
                            DetailRow("Fin", r.endDate)
                            HorizontalDivider(Modifier.padding(vertical = 8.dp))
                            DetailRow("Total pagado", "S/ ${r.total}", bold = true)
                        }
                    }

                    Spacer(Modifier.weight(1f))
                    if (r.status == "En curso") {
                        WPButton("Ver viaje en vivo", onClick = onTrack)
                        Spacer(Modifier.height(10.dp))
                    }
                    if (r.status == "Finalizado") {
                        WPButton("Calificar viaje", onClick = onRate)
                        Spacer(Modifier.height(10.dp))
                    }
                    WPOutlinedButton("Cerrar", onClick = onBack)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.weight(1f))
        Text(value, fontWeight = if (bold) FontWeight.Bold else FontWeight.SemiBold)
    }
}
