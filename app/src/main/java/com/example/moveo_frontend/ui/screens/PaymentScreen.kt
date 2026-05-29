package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
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
import com.example.moveo_frontend.ui.viewmodel.PaymentViewModel
import com.example.moveo_frontend.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(id: String, onBack: () -> Unit, onSuccess: () -> Unit) {
    val vm: PaymentViewModel = viewModel()
    val vehicleState by vm.vehicle.collectAsState()
    val paymentState by vm.payment.collectAsState()
    val methods by vm.methods.collectAsState()

    LaunchedEffect(id) { vm.load(id) }
    LaunchedEffect(paymentState) { /* trigger recomposition only */ }

    var method by remember { mutableStateOf("Yape") }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Pago seguro") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
        })
    }) { padding ->
        Box(Modifier.padding(padding)) {
            if (paymentState is UiState.Success) {
                Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(96.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("¡Reserva confirmada!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Recibirás los detalles por correo en unos minutos.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(32.dp))
                    WPButton("Volver al inicio", onClick = onSuccess)
                }
            } else {
                StateContainer(vehicleState, onRetry = { vm.load(id) }) { v ->
                    val rental = v.pricePerDay * 3
                    val fee = (rental * 0.05).toInt()
                    val deposit = 100
                    val total = rental + fee + deposit
                    Column(Modifier.padding(20.dp)) {
                        Text("Resumen", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(10.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp),
                            tonalElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                SummaryRow("Vehículo", "${v.brand} ${v.model}")
                                SummaryRow("Período", "3 días")
                                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                                SummaryRow("Alquiler (3 días)", "S/ $rental")
                                SummaryRow("Servicio WheelsPe (5%)", "S/ $fee")
                                SummaryRow("Garantía (Escrow)", "S/ $deposit")
                                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                                SummaryRow("Total", "S/ $total", bold = true)
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                        Text("Método de pago", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(10.dp))
                        val available = if (methods.isEmpty())
                            listOf("Yape" to "Pago instantáneo", "Plin" to "Pago instantáneo", "Visa **** 4521" to "Tarjeta de crédito")
                        else methods.map { it.display to it.type }
                        available.forEach { (name, subtitle) ->
                            PaymentOption(name, subtitle, method == name) { method = name }
                            Spacer(Modifier.height(8.dp))
                        }

                        if (paymentState is UiState.Error) {
                            Spacer(Modifier.height(8.dp))
                            Text((paymentState as UiState.Error).message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                        }
                        Spacer(Modifier.weight(1f))
                        WPButton(
                            text = if (paymentState is UiState.Loading) "Procesando..." else "Pagar S/ $total con $method",
                            enabled = paymentState !is UiState.Loading,
                            onClick = {
                                val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
                                val end = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(
                                    java.util.Date(System.currentTimeMillis() + 3L * 24 * 3600 * 1000)
                                )
                                vm.pay(v.id, method, total, today, end)
                            }
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(label, color = if (bold) Color.Unspecified else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
        Spacer(Modifier.weight(1f))
        Text(value, fontWeight = if (bold) FontWeight.Bold else FontWeight.SemiBold, color = if (bold) MaterialTheme.colorScheme.primary else Color.Unspecified)
    }
}

@Composable
private fun PaymentOption(name: String, subtitle: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .border(if (selected) 2.dp else 1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Icon(Icons.Default.CreditCard, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.SemiBold)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        RadioButton(selected = selected, onClick = onClick)
    }
}
