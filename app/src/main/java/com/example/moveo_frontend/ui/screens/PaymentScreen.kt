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
import com.example.moveo_frontend.data.MockData
import com.example.moveo_frontend.ui.components.WPButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(id: String, onBack: () -> Unit, onSuccess: () -> Unit) {
    val v = MockData.vehicles.firstOrNull { it.id == id } ?: return
    val rental = v.pricePerDay * 3
    val fee = (rental * 0.05).toInt()
    val deposit = 100
    val total = rental + fee + deposit
    var method by remember { mutableStateOf("Yape") }
    var paid by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Pago seguro") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
        })
    }) { padding ->
        if (paid) {
            Column(
                Modifier.padding(padding).fillMaxSize().padding(24.dp),
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
            Column(Modifier.padding(padding).padding(20.dp)) {
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
                PaymentOption("Yape", "Pago instantáneo", method == "Yape") { method = "Yape" }
                Spacer(Modifier.height(8.dp))
                PaymentOption("Plin", "Pago instantáneo", method == "Plin") { method = "Plin" }
                Spacer(Modifier.height(8.dp))
                PaymentOption("Visa **** 4521", "Tarjeta de crédito", method == "Visa") { method = "Visa" }

                Spacer(Modifier.weight(1f))
                WPButton("Pagar S/ $total con $method", onClick = { paid = true })
                Spacer(Modifier.height(12.dp))
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
