package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.data.Vehicle
import com.example.moveo_frontend.ui.components.RatingChip
import com.example.moveo_frontend.ui.components.StateContainer
import com.example.moveo_frontend.ui.components.VerifiedBadge
import com.example.moveo_frontend.ui.viewmodel.VehiclesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(onBack: () -> Unit, onVehicleClick: (String) -> Unit) {
    val vm: VehiclesViewModel = viewModel()
    val state by vm.state.collectAsState()
    val filter by vm.filter.collectAsState()
    val filters = listOf("Todos", "Compacto", "Sedán", "SUV")

    Scaffold(topBar = {
        TopAppBar(title = { Text("Catálogo de vehículos") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
        }, actions = { IconButton(onClick = { vm.load() }) { Icon(Icons.Default.Search, null) } })
    }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { f ->
                    FilterChip(
                        selected = f == filter,
                        onClick = { vm.setFilter(f) },
                        label = { Text(f) }
                    )
                }
            }
            StateContainer(state, onRetry = { vm.load() }) { vehicles ->
                if (vehicles.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay vehículos disponibles", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(vehicles) { v ->
                            VehicleListItem(v) { onVehicleClick(v.id) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VehicleListItem(v: Vehicle, onClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(Modifier.padding(12.dp)) {
            Box(
                Modifier
                    .size(96.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) { Text(v.imageEmoji, fontSize = 50.sp) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${v.brand} ${v.model}", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    RatingChip(v.rating)
                }
                Text("${v.year} · ${v.transmission} · ${v.seats} asientos", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(2.dp))
                    Text(v.location, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("S/ ${v.pricePerDay}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("/día", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.weight(1f))
                    if (v.ownerVerified) VerifiedBadge(v.ownerName)
                }
            }
        }
    }
}
