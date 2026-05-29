package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.example.moveo_frontend.ui.viewmodel.VehicleDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailScreen(id: String, onBack: () -> Unit, onReserve: (String) -> Unit, onChat: (String) -> Unit) {
    val vm: VehicleDetailViewModel = viewModel()
    val state by vm.state.collectAsState()
    LaunchedEffect(id) { vm.load(id) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Detalle del auto") }, navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            }, actions = {
                IconButton(onClick = { onChat(id) }) {
                    Icon(Icons.Filled.Chat, contentDescription = "Chat")
                }
            })
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            StateContainer(state, onRetry = { vm.load(id) }) { v ->
                val scroll = rememberScrollState()
                Column {
                    Column(Modifier.weight(1f).verticalScroll(scroll)) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) { Text(v.imageEmoji, fontSize = 120.sp) }

                        Column(Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("${v.brand} ${v.model}", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                                    Text("${v.year} · ${v.type}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                RatingChip(v.rating)
                            }
                            Spacer(Modifier.height(20.dp))

                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Spec(Icons.Default.Settings, v.transmission, "Transmisión")
                                Spec(Icons.Default.AirlineSeatReclineNormal, "${v.seats}", "Asientos")
                                Spec(Icons.Default.LocalGasStation, v.fuel, "Combustible")
                            }
                            Spacer(Modifier.height(24.dp))

                            Text("Propietario", fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Box(
                                    Modifier
                                        .size(44.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) { Text(v.ownerName.first().toString(), color = Color.White, fontWeight = FontWeight.Bold) }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(v.ownerName, fontWeight = FontWeight.SemiBold)
                                    Text("Propietario verificado", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                VerifiedBadge()
                            }
                            Spacer(Modifier.height(20.dp))

                            Text("Descripción", fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(6.dp))
                            Text(v.description, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(20.dp))

                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Shield, null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text("Garantía retenida en Escrow", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text("Tu dinero se libera al propietario tras la devolución", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                    Surface(tonalElevation = 4.dp) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("S/ ${v.pricePerDay}", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = MaterialTheme.colorScheme.primary)
                                Text("por día", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            WPButton("Reservar", onClick = { onReserve(v.id) }, modifier = Modifier.weight(1.5f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Spec(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
