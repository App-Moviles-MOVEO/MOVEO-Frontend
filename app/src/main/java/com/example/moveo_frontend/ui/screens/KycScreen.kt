package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.ui.components.WPButton
import com.example.moveo_frontend.ui.viewmodel.KycViewModel
import com.example.moveo_frontend.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycScreen(onFinish: () -> Unit) {
    val vm: KycViewModel = viewModel()
    val state by vm.state.collectAsState()
    var step by remember { mutableStateOf(0) }
    val steps = listOf(
        Triple("Frente del DNI", "Coloca el documento dentro del marco", Icons.Default.CreditCard),
        Triple("Reverso del DNI", "Asegúrate que sea legible", Icons.Default.CreditCard),
        Triple("Selfie de validación", "Mira a la cámara y mantén el rostro centrado", Icons.Default.Face),
        Triple("¡Verificación enviada!", "Tu identidad se verificará en pocos minutos", Icons.Default.Check)
    )
    val (title, subtitle, icon) = steps[step]

    LaunchedEffect(state) {
        if (state is UiState.Success) onFinish()
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Verificación KYC · Paso ${step + 1} de 4") }) }) { padding ->
        Column(
            Modifier.padding(padding).padding(24.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                progress = { (step + 1) / 4f },
                modifier = Modifier.fillMaxWidth().height(8.dp)
            )
            Spacer(Modifier.height(36.dp))

            Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(
                subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(40.dp))

            ScannerFrame(icon)

            if (state is UiState.Error) {
                Spacer(Modifier.height(12.dp))
                Text((state as UiState.Error).message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(Modifier.weight(1f))
            WPButton(
                text = when {
                    state is UiState.Loading -> "Enviando..."
                    step < steps.size - 1 -> "Capturar"
                    else -> "Finalizar"
                },
                enabled = state !is UiState.Loading,
                onClick = {
                    if (step < steps.size - 1) {
                        step++
                    } else {
                        // En el backend real subirías los archivos capturados. Aquí avanzamos al finalizar.
                        onFinish()
                    }
                }
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ScannerFrame(icon: ImageVector) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, modifier = Modifier.size(96.dp), tint = MaterialTheme.colorScheme.primary)
    }
}
