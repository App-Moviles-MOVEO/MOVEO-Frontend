package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moveo_frontend.data.MockData
import com.example.moveo_frontend.ui.components.RatingChip
import com.example.moveo_frontend.ui.components.SectionTitle
import com.example.moveo_frontend.ui.theme.BluePrimary

@Composable
fun HomeScreen(
    onCatalog: () -> Unit,
    onCarpoolSearch: () -> Unit,
    onSafety: () -> Unit,
    onRewards: () -> Unit,
    onVehicleClick: (String) -> Unit
) {
    val user = MockData.currentUser
    val scroll = rememberScrollState()
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scroll)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .size(48.dp)
                    .background(BluePrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(user.name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Hola, ${user.name.split(" ").first()} 👋", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("¿A dónde vas hoy?", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = {}) { Icon(Icons.Default.Notifications, null) }
        }
        Spacer(Modifier.height(16.dp))

        // search bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(14.dp),
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Buscar destino o vehículo", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(20.dp))

        SectionTitle("Accesos rápidos")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickAction(Icons.Default.DirectionsCar, "Alquilar\nauto", Modifier.weight(1f), onCatalog)
            QuickAction(Icons.Default.Route, "Buscar\nruta", Modifier.weight(1f), onCarpoolSearch)
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickAction(Icons.Default.Shield, "Contactos de\nconfianza", Modifier.weight(1f), onSafety)
            QuickAction(Icons.Default.CardGiftcard, "Recompensas\n(${user.rewardPoints} pts)", Modifier.weight(1f), onRewards)
        }
        Spacer(Modifier.height(24.dp))

        SectionTitle("Cerca de ti")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(MockData.vehicles) { v ->
                NearbyCard(v.imageEmoji, "${v.brand} ${v.model}", "S/ ${v.pricePerDay}/día", v.rating) {
                    onVehicleClick(v.id)
                }
            }
        }
        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun QuickAction(icon: ImageVector, label: String, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 1.dp,
        modifier = modifier.height(96.dp).clickable(onClick = onClick)
    ) {
        Column(
            Modifier.padding(12.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun NearbyCard(emoji: String, title: String, price: String, rating: Double, onClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.width(180.dp).clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(12.dp)) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 44.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(price, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                Spacer(Modifier.weight(1f))
                RatingChip(rating)
            }
        }
    }
}

