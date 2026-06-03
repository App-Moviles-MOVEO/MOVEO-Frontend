package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.ui.components.IconThumb
import com.example.moveo_frontend.ui.components.StateContainer
import com.example.moveo_frontend.ui.components.WPOutlinedButton
import com.example.moveo_frontend.ui.viewmodel.MyListingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListingsScreen(
    onBack: () -> Unit,
    onPublishVehicle: () -> Unit,
    onPublishCarpool: () -> Unit
) {
    val vm: MyListingsViewModel = viewModel()
    val vehicles by vm.vehicles.collectAsState()
    val routes by vm.routes.collectAsState()
    var tab by remember { mutableStateOf(0) }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Mis publicaciones") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
        })
    }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 },
                    text = { Text("Mis autos") },
                    icon = { Icon(Icons.Default.DirectionsCar, null) })
                Tab(selected = tab == 1, onClick = { tab = 1 },
                    text = { Text("Mis viajes") },
                    icon = { Icon(Icons.Default.Route, null) })
            }
            Box(Modifier.weight(1f)) {
                if (tab == 0) {
                    StateContainer(vehicles, onRetry = { vm.load() }) { list ->
                        if (list.isEmpty()) {
                            EmptyListings("Aún no publicas autos", "Publicar un auto", onPublishVehicle)
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(list, key = { it.id }) { v ->
                                    ListingCard(Icons.Filled.DirectionsCar, "${v.brand} ${v.model} · ${v.year}",
                                        "${v.location} · ${v.type}", "S/ ${v.pricePerDay}/día")
                                }
                                item {
                                    Spacer(Modifier.height(4.dp))
                                    WPOutlinedButton("Publicar otro auto", onPublishVehicle)
                                }
                            }
                        }
                    }
                } else {
                    StateContainer(routes, onRetry = { vm.load() }) { list ->
                        if (list.isEmpty()) {
                            EmptyListings("Aún no publicas viajes", "Publicar un viaje", onPublishCarpool)
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(list, key = { it.id }) { r ->
                                    ListingCard(Icons.Filled.Route, "${r.origin} → ${r.destination}",
                                        "${r.departureTime} · ${r.date} · ${r.seatsAvailable} cupos", "S/ ${r.pricePerSeat}/asiento")
                                }
                                item {
                                    Spacer(Modifier.height(4.dp))
                                    WPOutlinedButton("Publicar otro viaje", onPublishCarpool)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ListingCard(icon: ImageVector, title: String, subtitle: String, price: String) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconThumb(icon, size = 64.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text(price, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            }
            AssistChip(onClick = {}, label = { Text("Activo") })
        }
    }
}

@Composable
private fun EmptyListings(message: String, cta: String, onClick: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(12.dp))
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(20.dp))
        WPOutlinedButton(cta, onClick)
    }
}