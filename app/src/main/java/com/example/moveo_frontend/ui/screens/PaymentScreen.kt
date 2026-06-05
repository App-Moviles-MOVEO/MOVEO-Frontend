package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
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
import com.example.moveo_frontend.ui.viewmodel.PaymentViewModel
import com.example.moveo_frontend.ui.viewmodel.UiState
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val DAY_MS = 24L * 60 * 60 * 1000

private fun fmt(millis: Long): String =
    SimpleDateFormat("EEE dd MMM yyyy", Locale.forLanguageTag("es")).format(Date(millis))

// El backend espera ISO 8601 UTC (ej. "2026-06-10T09:00:00Z").
private fun fmtIso(millis: Long): String =
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date(millis))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(id: String, onBack: () -> Unit, onSuccess: () -> Unit) {
    val vm: PaymentViewModel = viewModel()
    val vehicleState by vm.vehicle.collectAsState()
    val paymentState by vm.payment.collectAsState()
    val clientSecret by vm.clientSecret.collectAsState()

    LaunchedEffect(id) { vm.load(id) }

    // Stripe PaymentSheet: recolecta la tarjeta y confirma el pago de forma segura.
    val paymentSheet = rememberPaymentSheet { result ->
        when (result) {
            is PaymentSheetResult.Completed -> vm.onPaymentSucceeded()
            is PaymentSheetResult.Canceled -> vm.onPaymentCanceled()
            is PaymentSheetResult.Failed -> vm.onPaymentFailed(result.error.message ?: "El pago falló")
        }
    }

    // Cuando el ViewModel emite el client_secret, abrimos el sheet de Stripe.
    LaunchedEffect(clientSecret) {
        val secret = clientSecret ?: return@LaunchedEffect
        vm.onSheetPresented()
        paymentSheet.presentWithPaymentIntent(
            secret,
            PaymentSheet.Configuration(merchantDisplayName = "MOVEO")
        )
    }

    var startMillis by remember { mutableStateOf(System.currentTimeMillis() + DAY_MS) }
    var days by remember { mutableStateOf(3) }
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Pago seguro") }, navigationIcon = {
            WPBackButton(onClick = onBack)
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
                    Text("Tu pago se procesó correctamente. Recibirás los detalles por correo.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(32.dp))
                    WPButton("Volver al inicio", onClick = onSuccess)
                }
            } else {
                StateContainer(vehicleState, onRetry = { vm.load(id) }) { v ->
                    val endMillis = startMillis + days * DAY_MS
                    val rental = v.pricePerDay * days
                    val fee = (rental * 0.05).toInt()
                    val deposit = 100
                    val total = rental + fee + deposit

                    Column(Modifier.fillMaxSize()) {
                        Column(
                            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp)
                        ) {
                            // ---- Selección de fechas ----
                            Text("Fechas de alquiler", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(10.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp),
                                tonalElevation = 1.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(Modifier.padding(14.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }
                                    ) {
                                        Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(Modifier.width(12.dp))
                                        Column(Modifier.weight(1f)) {
                                            Text("Día de inicio", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(fmt(startMillis), fontWeight = FontWeight.SemiBold)
                                        }
                                        Text("Cambiar", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    }
                                    HorizontalDivider(Modifier.padding(vertical = 12.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Column(Modifier.weight(1f)) {
                                            Text("Cantidad de días", fontWeight = FontWeight.SemiBold)
                                            Text("Devolución: ${fmt(endMillis)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        OutlinedIconButton(onClick = { if (days > 1) days-- }, enabled = days > 1) {
                                            Icon(Icons.Default.Remove, "Quitar día")
                                        }
                                        Text("$days", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 12.dp))
                                        OutlinedIconButton(onClick = { if (days < 30) days++ }, enabled = days < 30) {
                                            Icon(Icons.Default.Add, "Agregar día")
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(24.dp))
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
                                    SummaryRow("Período", "$days ${if (days == 1) "día" else "días"}")
                                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                                    SummaryRow("Alquiler (S/ ${v.pricePerDay} x $days)", "S/ $rental")
                                    SummaryRow("Servicio MOVEO (5%)", "S/ $fee")
                                    SummaryRow("Garantía (Escrow)", "S/ $deposit")
                                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                                    SummaryRow("Total", "S/ $total", bold = true)
                                }
                            }

                            // ---- Explicación de la garantía ----
                            Spacer(Modifier.height(16.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(Modifier.padding(14.dp)) {
                                    Icon(Icons.Default.Shield, null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text("¿Qué es la garantía (Escrow)?", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "Los S/ $deposit de garantía NO son un cobro extra: quedan retenidos de forma segura mientras dura el alquiler. " +
                                                "Si devuelves el auto sin daños y a tiempo, se te reembolsan completos al finalizar. " +
                                                "Solo se descuentan si hay daños, multas o devolución tardía. Tu pago al propietario también se libera recién tras la devolución.",
                                            fontSize = 12.sp,
                                            lineHeight = 17.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // ---- Info de pago Stripe ----
                            Spacer(Modifier.height(16.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp),
                                tonalElevation = 1.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text("Pago protegido con Stripe", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                        Text(
                                            "Aceptamos Visa, Mastercard y más. Tus datos de tarjeta los procesa Stripe; la app nunca los almacena.",
                                            fontSize = 12.sp, lineHeight = 17.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            if (paymentState is UiState.Error) {
                                Spacer(Modifier.height(12.dp))
                                Text((paymentState as UiState.Error).message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                            }
                        }

                        // Botones fijos abajo
                        Surface(tonalElevation = 4.dp) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                WPButton(
                                    text = if (paymentState is UiState.Loading) "Procesando..." else "Pagar S/ $total con tarjeta",
                                    enabled = paymentState !is UiState.Loading,
                                    onClick = {
                                        vm.preparePayment(v.id, "card", total, fmtIso(startMillis), fmtIso(endMillis))
                                    }
                                )
                                // Pago rápido por Yape: culmina la transacción directo, sin pasar por Stripe.
                                WPOutlinedButton(
                                    text = "Pagar con Yape (rápido)",
                                    onClick = {
                                        vm.payDirect(v.id, "yape", total, fmtIso(startMillis), fmtIso(endMillis))
                                    }
                                )
                            }
                        }
                    }

                    if (showDatePicker) {
                        val dpState = rememberDatePickerState(
                            initialSelectedDateMillis = startMillis,
                            selectableDates = object : SelectableDates {
                                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                                    utcTimeMillis >= System.currentTimeMillis() - DAY_MS
                            }
                        )
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    dpState.selectedDateMillis?.let { startMillis = it }
                                    showDatePicker = false
                                }) { Text("Aceptar") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                            }
                        ) { DatePicker(state = dpState) }
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