package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.data.remote.dto.PublishVehicleRequest
import com.example.moveo_frontend.ui.components.WPButton
import com.example.moveo_frontend.ui.viewmodel.PublishVehicleViewModel
import com.example.moveo_frontend.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishVehicleScreen(onBack: () -> Unit, onPublished: () -> Unit) {
    val vm: PublishVehicleViewModel = viewModel()
    val state by vm.state.collectAsState()

    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Sedán") }
    var transmission by remember { mutableStateOf("Mecánico") }
    var seats by remember { mutableStateOf("5") }
    var fuel by remember { mutableStateOf("Gasolina") }
    var description by remember { mutableStateOf("") }
    val scroll = rememberScrollState()

    LaunchedEffect(state) {
        if (state is UiState.Success) onPublished()
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Publicar vehículo") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
        })
    }) { padding ->
        Column(Modifier.padding(padding).padding(20.dp).verticalScroll(scroll)) {
            Text("Datos del vehículo", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("Marca") }, modifier = Modifier.weight(1f), singleLine = true)
                OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text("Modelo") }, modifier = Modifier.weight(1f), singleLine = true)
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = year, onValueChange = { year = it.filter(Char::isDigit).take(4) }, label = { Text("Año") }, modifier = Modifier.weight(1f), singleLine = true)
                OutlinedTextField(value = price, onValueChange = { price = it.filter(Char::isDigit) }, label = { Text("Precio/día (S/)") }, modifier = Modifier.weight(1f), singleLine = true)
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Ubicación (distrito)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            Spacer(Modifier.height(16.dp))
            Text("Categoría", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                items(listOf("Compacto", "Sedán", "SUV")) {
                    FilterChip(selected = type == it, onClick = { type = it }, label = { Text(it) })
                }
            }
            Text("Transmisión", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                items(listOf("Mecánico", "Automático")) {
                    FilterChip(selected = transmission == it, onClick = { transmission = it }, label = { Text(it) })
                }
            }
            Text("Combustible", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                items(listOf("Gasolina", "Diésel", "GLP", "Eléctrico")) {
                    FilterChip(selected = fuel == it, onClick = { fuel = it }, label = { Text(it) })
                }
            }

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = seats, onValueChange = { seats = it.filter(Char::isDigit) }, label = { Text("Asientos") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp))

            if (state is UiState.Error) {
                Spacer(Modifier.height(12.dp))
                Text((state as UiState.Error).message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(Modifier.height(24.dp))
            WPButton(
                text = if (state is UiState.Loading) "Publicando..." else "Publicar",
                enabled = state !is UiState.Loading && brand.isNotBlank() && model.isNotBlank() && price.isNotBlank() && location.isNotBlank(),
                onClick = {
                    vm.publish(
                        PublishVehicleRequest(
                            brand = brand,
                            model = model,
                            year = year.toIntOrNull() ?: 2024,
                            pricePerDay = price.toIntOrNull() ?: 0,
                            location = location,
                            type = type,
                            transmission = transmission,
                            seats = seats.toIntOrNull() ?: 5,
                            fuel = fuel,
                            description = description
                        )
                    )
                }
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
