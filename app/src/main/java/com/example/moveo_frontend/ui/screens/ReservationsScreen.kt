package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.data.CarpoolBooking
import com.example.moveo_frontend.data.Reservation
import com.example.moveo_frontend.ui.components.CarpoolThumb
import com.example.moveo_frontend.ui.components.StateContainer
import com.example.moveo_frontend.ui.components.VehicleThumb
import com.example.moveo_frontend.ui.theme.GreenSuccess
import com.example.moveo_frontend.ui.theme.OrangeReward
import com.example.moveo_frontend.ui.viewmodel.ReservationsViewModel

@Composable
fun ReservationsScreen(onClick: (String) -> Unit = {}) {
    val vm: ReservationsViewModel = viewModel()
    val state by vm.state.collectAsState()
    val carpools by vm.carpools.collectAsState()
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Text(
            "Mis reservas",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            modifier = Modifier.padding(20.dp)
        )
        StateContainer(state, onRetry = { vm.load() }) { rentals ->
            if (rentals.isEmpty() && carpools.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aún no tienes reservas", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (rentals.isNotEmpty()) {
                        item { SectionHeader("Alquileres") }
                        items(rentals) { r -> RentalCard(r, onClick) }
                    }
                    if (carpools.isNotEmpty()) {
                        item { SectionHeader("Viajes compartidos") }
                        items(carpools) { b -> CarpoolBookingCard(b) }
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
    )
}

@Composable
private fun RentalCard(r: Reservation, onClick: (String) -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().clickable { onClick(r.id) }
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            VehicleThumb(size = 48.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(r.vehicleName, fontWeight = FontWeight.SemiBold)
                Text("${r.startDate} - ${r.endDate}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                StatusChip(r.status)
            }
            Text("S/ ${r.total}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun CarpoolBookingCard(b: CarpoolBooking) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            CarpoolThumb(size = 48.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("${b.origin} → ${b.destination}", fontWeight = FontWeight.SemiBold)
                Text(
                    "${b.departureTime} · ${b.date} · ${b.seats} ${if (b.seats == 1) "asiento" else "asientos"}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                StatusChip("Reservado")
            }
            Text("S/ ${b.total}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val color = when (status) {
        "Confirmado", "Reservado" -> GreenSuccess
        "En curso" -> OrangeReward
        else -> Color.Gray
    }
    Text(
        status,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White,
        modifier = Modifier
            .background(color, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}