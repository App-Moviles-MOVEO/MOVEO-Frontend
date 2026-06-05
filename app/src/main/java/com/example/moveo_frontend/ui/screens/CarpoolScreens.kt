package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.data.CarpoolRoute
import com.example.moveo_frontend.data.remote.dto.PublishRouteRequest
import com.example.moveo_frontend.ui.components.WPBackButton
import com.example.moveo_frontend.ui.components.RatingChip
import com.example.moveo_frontend.ui.components.StateContainer
import com.example.moveo_frontend.ui.components.VerifiedBadge
import com.example.moveo_frontend.ui.components.WPButton
import com.example.moveo_frontend.ui.viewmodel.CarpoolViewModel
import com.example.moveo_frontend.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarpoolSearchScreen(onBack: () -> Unit, onRouteClick: (String) -> Unit, onPublish: (() -> Unit)? = null) {
    val vm: CarpoolViewModel = viewModel()
    val state by vm.routes.collectAsState()
    val onlyWomen by vm.onlyWomen.collectAsState()
    val onlyVerified by vm.onlyVerified.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Buscar ruta") }, navigationIcon = {
                WPBackButton(onClick = onBack)
            })
        },
        floatingActionButton = {
            if (onPublish != null) {
                ExtendedFloatingActionButton(
                    onClick = onPublish,
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Publicar viaje") }
                )
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = onlyWomen, onClick = { vm.setOnlyWomen(!onlyWomen) }, label = { Text("Solo mujeres") })
                        FilterChip(selected = onlyVerified, onClick = { vm.setOnlyVerified(!onlyVerified) }, label = { Text("Verificados") })
                    }
                }
            }
            StateContainer(state, onRetry = { vm.load() }) { routes ->
                if (routes.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay rutas disponibles", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(routes) { r ->
                            RouteCard(r) { onRouteClick(r.id) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteCard(r: CarpoolRoute, onClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) { Text(r.driverName.first().toString(), color = Color.White, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(r.driverName, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(6.dp))
                        if (r.verified) VerifiedBadge(r.community)
                    }
                    Text(r.vehicleModel, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                RatingChip(r.driverRating)
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MyLocation, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(6.dp))
                Text(r.origin, fontSize = 13.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                Spacer(Modifier.width(6.dp))
                Text(r.destination, fontSize = 13.sp)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                InfoChip(Icons.Default.Schedule, "${r.departureTime} · ${r.date}")
                Spacer(Modifier.width(6.dp))
                InfoChip(Icons.Default.AirlineSeatReclineNormal, "${r.seatsAvailable} cupos")
                Spacer(Modifier.weight(1f))
                Text("S/ ${r.pricePerSeat}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(icon, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(4.dp))
        Text(text, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarpoolPublishScreen(onBack: () -> Unit, onPublished: () -> Unit) {
    val vm: CarpoolViewModel = viewModel()
    val state by vm.publish.collectAsState()

    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("Vie 9 may") }
    var time by remember { mutableStateOf("07:15") }
    var seats by remember { mutableStateOf("3") }
    var price by remember { mutableStateOf("6") }
    var recurring by remember { mutableStateOf(true) }
    val scroll = rememberScrollState()

    LaunchedEffect(state) {
        if (state is UiState.Success) onPublished()
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Publicar ruta") }, navigationIcon = {
            WPBackButton(onClick = onBack)
        })
    }) { padding ->
        Column(Modifier.padding(padding).padding(20.dp).verticalScroll(scroll)) {
            OutlinedTextField(value = origin, onValueChange = { origin = it }, label = { Text("Origen") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = destination, onValueChange = { destination = it }, label = { Text("Destino") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Fecha") }, modifier = Modifier.weight(1f), singleLine = true)
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Hora") }, modifier = Modifier.weight(1f), singleLine = true)
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = seats, onValueChange = { seats = it.filter(Char::isDigit) }, label = { Text("Asientos") }, modifier = Modifier.weight(1f), singleLine = true)
                OutlinedTextField(value = price, onValueChange = { price = it.filter(Char::isDigit) }, label = { Text("Precio por asiento") }, modifier = Modifier.weight(1f), singleLine = true)
            }
            Spacer(Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .clickable { recurring = !recurring }
                    .padding(14.dp)
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Ruta recurrente (L-V)", fontWeight = FontWeight.SemiBold)
                    Text("Se publicará automáticamente cada semana", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = recurring, onCheckedChange = { recurring = it })
            }
            if (state is UiState.Error) {
                Spacer(Modifier.height(12.dp))
                Text((state as UiState.Error).message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }
            Spacer(Modifier.height(28.dp))
            WPButton(
                text = if (state is UiState.Loading) "Publicando..." else "Publicar ruta",
                onClick = {
                    vm.publishRoute(
                        PublishRouteRequest(
                            origin = origin,
                            destination = destination,
                            date = date,
                            time = time,
                            seats = seats.toIntOrNull() ?: 1,
                            price = price.toIntOrNull() ?: 0,
                            recurring = recurring
                        )
                    )
                },
                enabled = origin.isNotBlank() && destination.isNotBlank() && state !is UiState.Loading
            )
        }
    }
}
