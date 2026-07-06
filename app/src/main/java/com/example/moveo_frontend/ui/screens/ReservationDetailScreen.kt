package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.data.Reservation
import com.example.moveo_frontend.ui.components.WPBackButton
import com.example.moveo_frontend.ui.components.StateContainer
import com.example.moveo_frontend.ui.components.WPButton
import com.example.moveo_frontend.ui.components.WPOutlinedButton
import com.example.moveo_frontend.ui.viewmodel.ReservationsViewModel
import com.example.moveo_frontend.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailScreen(id: String, onBack: () -> Unit, onTrack: () -> Unit, onRate: () -> Unit) {
    val vm: ReservationsViewModel = viewModel()
    val state by vm.detail.collectAsState()
    val cancelState by vm.cancelState.collectAsState()
    val advanceState by vm.advanceState.collectAsState()
    val invoiceState by vm.invoice.collectAsState()
    var showCancelDialog by remember { mutableStateOf(false) }
    LaunchedEffect(id) { vm.loadDetail(id) }

    // Resultado de la cancelación (US54) y del reembolso automático (US26/US33).
    when (val cs = cancelState) {
        is UiState.Success -> AlertDialog(
            onDismissRequest = { vm.resetCancel() },
            confirmButton = { TextButton(onClick = { vm.resetCancel() }) { Text("Entendido") } },
            title = { Text("Reserva cancelada") },
            text = {
                Text(
                    when {
                        cs.data.refundPercent == 0 ->
                            "Según la política de cancelación (<24 h antes del inicio) no corresponde reembolso."
                        cs.data.refundAmount <= 0 ->
                            "La reserva no tenía pagos registrados, así que no hay nada que reembolsar."
                        cs.data.refundProcessed ->
                            "Se procesó automáticamente un reembolso de S/ ${cs.data.refundAmount} (${cs.data.refundPercent}% del total). Lo verás reflejado en tu método de pago."
                        else ->
                            "Corresponde un reembolso de S/ ${cs.data.refundAmount} (${cs.data.refundPercent}%). No pudimos procesarlo automáticamente; se reintentará y te notificaremos."
                    }
                )
            }
        )
        is UiState.Error -> AlertDialog(
            onDismissRequest = { vm.resetCancel() },
            confirmButton = { TextButton(onClick = { vm.resetCancel() }) { Text("Cerrar") } },
            title = { Text("No se pudo cancelar") },
            text = { Text(cs.message) }
        )
        else -> {}
    }

    // US25: comprobante digital emitido por el backend.
    when (val inv = invoiceState) {
        is UiState.Success -> InvoiceDialog(inv.data, onDismiss = { vm.resetInvoice() })
        is UiState.Error -> AlertDialog(
            onDismissRequest = { vm.resetInvoice() },
            confirmButton = { TextButton(onClick = { vm.resetInvoice() }) { Text("Cerrar") } },
            title = { Text("Comprobante no disponible") },
            text = { Text(inv.message) }
        )
        else -> {}
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Detalle de reserva") }, navigationIcon = {
            WPBackButton(onClick = onBack)
        })
    }) { padding ->
        Box(Modifier.padding(padding)) {
            StateContainer(state, onRetry = { vm.loadDetail(id) }) { r ->
                Column(Modifier.padding(20.dp).fillMaxSize()) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(14.dp),
                        tonalElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(64.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) { Icon(Icons.Default.DirectionsCar, null, tint = MaterialTheme.colorScheme.primary) }
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(r.vehicleName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("${r.startDate} → ${r.endDate}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    r.status,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(14.dp),
                        tonalElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            DetailRow("ID reserva", r.id)
                            DetailRow("Inicio", r.startDate)
                            DetailRow("Fin", r.endDate)
                            HorizontalDivider(Modifier.padding(vertical = 8.dp))
                            DetailRow("Total pagado", "S/ ${r.total}", bold = true)
                        }
                    }

                    // US09: PIN de entrega. El arrendatario lo muestra al propietario
                    // al recibir el vehículo; el Owner lo valida con la misma fórmula.
                    if (r.status == "Aceptado" || r.status == "En curso") {
                        Spacer(Modifier.height(16.dp))
                        DeliveryPinCard(r.id)
                    }

                    if (r.cancellable) {
                        Spacer(Modifier.height(16.dp))
                        CancellationPolicyCard(r)
                    }

                    Spacer(Modifier.weight(1f))

                    if (advanceState is UiState.Error) {
                        Text(
                            (advanceState as UiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    val advancing = advanceState is UiState.Loading

                    // Avance del flujo de la reserva. Aceptar es acción del propietario:
                    // aquí se ofrece como demo mientras la app de owners no existe.
                    when (r.status) {
                        "Pendiente" -> {
                            WPButton(
                                if (advancing) "Procesando..." else "Aceptar reserva (demo propietario)",
                                enabled = !advancing,
                                onClick = { vm.advance(r.id, "accepted") }
                            )
                            Text(
                                "Demo: en producción esta acción la hace el propietario desde su app.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                        "Aceptado" -> {
                            WPButton(
                                if (advancing) "Procesando..." else "Iniciar viaje",
                                enabled = !advancing,
                                onClick = { vm.advance(r.id, "active") }
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                        "En curso" -> {
                            WPButton("Ver viaje en vivo", onClick = onTrack)
                            Spacer(Modifier.height(10.dp))
                            WPOutlinedButton(
                                if (advancing) "Procesando..." else "Finalizar viaje (libera el vehículo)",
                                enabled = !advancing,
                                onClick = { vm.advance(r.id, "completed") }
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                        "Finalizado" -> {
                            if (r.vehicleRated) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "Ya calificaste este viaje: ${"★".repeat(r.vehicleRating ?: 0)} (${r.vehicleRating}/5)",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            } else {
                                WPButton("Calificar viaje", onClick = onRate)
                            }
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                    // US25: comprobante disponible cuando la reserva tiene (o pudo tener) pago.
                    if (r.status in setOf("Aceptado", "En curso", "Finalizado")) {
                        WPOutlinedButton(
                            if (invoiceState is UiState.Loading) "Cargando comprobante..." else "Ver comprobante",
                            enabled = invoiceState !is UiState.Loading,
                            onClick = { vm.loadInvoice(r.id) }
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                    if (r.cancellable) {
                        OutlinedButton(
                            onClick = { showCancelDialog = true },
                            enabled = cancelState !is UiState.Loading,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (cancelState is UiState.Loading) "Cancelando..." else "Cancelar reserva")
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                    WPOutlinedButton("Cerrar", onClick = onBack)

                    if (showCancelDialog) {
                        CancelConfirmDialog(
                            reservation = r,
                            onConfirm = {
                                showCancelDialog = false
                                vm.cancel(r)
                            },
                            onDismiss = { showCancelDialog = false }
                        )
                    }
                }
            }
        }
    }
}

/** US25: comprobante digital con la numeración oficial del backend. */
@Composable
private fun InvoiceDialog(inv: com.example.moveo_frontend.data.remote.dto.InvoiceDto, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } },
        title = { Text("Comprobante") },
        text = {
            Column {
                DetailRow("N°", inv.invoiceNumber, bold = true)
                DetailRow("Cliente", inv.customer.fullName.ifBlank { "—" })
                DetailRow("Vehículo", inv.vehicle.name.ifBlank { "—" })
                inv.vehicle.licensePlate?.takeIf { it.isNotBlank() }?.let { DetailRow("Placa", it) }
                DetailRow("Días", "${inv.period.days}")
                inv.payment.method?.let { DetailRow("Método de pago", it) }
                inv.payment.transactionId?.takeIf { it.isNotBlank() }?.let { DetailRow("Transacción", it) }
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                DetailRow("Total", "${inv.amount.currency} ${inv.amount.total}", bold = true)
            }
        }
    )
}

/** US09: tarjeta con el PIN de entrega del viaje, derivado del id de la reserva. */
@Composable
private fun DeliveryPinCard(reservationId: String) {
    val pin = com.example.moveo_frontend.util.TripPin.forRental(reservationId)
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "PIN de entrega",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Muéstraselo al propietario para validar la entrega del vehículo.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                pin.toCharArray().joinToString("  "),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/** Política de cancelación con el reembolso que aplicaría ahora mismo (US54). */
@Composable
private fun CancellationPolicyCard(r: Reservation) {
    val pct = r.refundPercent()
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Política de cancelación", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            PolicyRow("Hasta 48 h antes del inicio", "100% de reembolso", pct == 100)
            PolicyRow("Entre 24 y 48 h antes", "50% de reembolso", pct == 50)
            PolicyRow("Menos de 24 h antes", "Sin reembolso", pct == 0)
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Text(
                if (pct > 0) "Si cancelas ahora: reembolso automático de S/ ${r.refundAmount()} ($pct%)"
                else "Si cancelas ahora no corresponde reembolso",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (pct > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun PolicyRow(rule: String, refund: String, active: Boolean) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(
            rule,
            fontSize = 13.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
            color = if (active) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.weight(1f))
        Text(
            refund,
            fontSize = 13.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
            color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CancelConfirmDialog(reservation: Reservation, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val pct = reservation.refundPercent()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Cancelar reserva?") },
        text = {
            Text(
                if (pct > 0)
                    "Se cancelará tu reserva de ${reservation.vehicleName} y se procesará automáticamente un reembolso de S/ ${reservation.refundAmount()} ($pct% del total pagado)."
                else
                    "Se cancelará tu reserva de ${reservation.vehicleName}. Según la política (<24 h antes del inicio) no corresponde reembolso."
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Sí, cancelar", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Volver") } }
    )
}

@Composable
private fun DetailRow(label: String, value: String, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.weight(1f))
        Text(value, fontWeight = if (bold) FontWeight.Bold else FontWeight.SemiBold)
    }
}
