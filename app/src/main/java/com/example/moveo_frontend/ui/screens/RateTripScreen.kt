package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.ui.components.WPButton
import com.example.moveo_frontend.ui.viewmodel.RateTripViewModel
import com.example.moveo_frontend.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateTripScreen(targetUserId: String, reservationId: String?, routeId: String?, onBack: () -> Unit, onDone: () -> Unit) {
    val vm: RateTripViewModel = viewModel()
    val state by vm.state.collectAsState()
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is UiState.Success) onDone()
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Calificar") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
        })
    }) { padding ->
        Column(Modifier.padding(padding).padding(24.dp).fillMaxSize()) {
            Text("¿Cómo fue tu viaje?", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                "Tu calificación ayuda a mantener la confianza en la comunidad.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                (1..5).forEach { i ->
                    val filled = i <= rating
                    Icon(
                        if (filled) Icons.Default.Star else Icons.Outlined.Star,
                        null,
                        tint = if (filled) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp).clickable { rating = i }
                    )
                }
            }
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Comentario (opcional)") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp)
            )

            if (state is UiState.Error) {
                Spacer(Modifier.height(12.dp))
                Text((state as UiState.Error).message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(Modifier.weight(1f))
            WPButton(
                text = if (state is UiState.Loading) "Enviando..." else "Enviar calificación",
                enabled = state !is UiState.Loading,
                onClick = { vm.submit(targetUserId, reservationId, routeId, rating, comment) }
            )
        }
    }
}
