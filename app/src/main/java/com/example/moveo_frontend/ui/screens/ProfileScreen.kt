package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.ui.components.SectionTitle
import com.example.moveo_frontend.ui.components.StateContainer
import com.example.moveo_frontend.ui.components.VerifiedBadge
import com.example.moveo_frontend.ui.viewmodel.AuthViewModel
import com.example.moveo_frontend.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onEditProfile: () -> Unit = {},
    onPaymentMethods: () -> Unit = {},
    onMyListings: () -> Unit = {},
    onSettings: () -> Unit = {},
    onHelp: () -> Unit = {}
) {
    val vm: ProfileViewModel = viewModel()
    val auth: AuthViewModel = viewModel()
    val state by vm.user.collectAsState()
    val reviews by vm.reviews.collectAsState()
    val scroll = rememberScrollState()

    Box(Modifier.fillMaxSize()) {
        StateContainer(state, onRetry = { vm.load() }) { user ->
            Column(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(scroll)
            ) {
                Surface(color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth()) {
                    Column(
                        Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            Modifier.size(88.dp).background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) { Text(user.name.firstOrNull()?.toString().orEmpty(), color = MaterialTheme.colorScheme.primary, fontSize = 36.sp, fontWeight = FontWeight.Bold) }
                        Spacer(Modifier.height(12.dp))
                        Text(user.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(user.email, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatChip(if (user.rating > 0.0) "${user.rating}★" else "Nuevo", "Rating")
                            Spacer(Modifier.width(12.dp))
                            StatChip("${user.tripsCompleted}", "Viajes")
                            Spacer(Modifier.width(12.dp))
                            StatChip("38kg", "CO₂ ahorrado")
                        }
                    }
                }

                Column(Modifier.padding(20.dp)) {
                    SectionTitle("Badges")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        user.badges.forEach { VerifiedBadge(it) }
                    }
                    Spacer(Modifier.height(20.dp))

                    SectionTitle("Reseñas recibidas")
                    if (reviews.isEmpty()) {
                        Text("Aún no tienes reseñas", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    } else {
                        reviews.forEach { ReviewCard(it.author, it.rating, it.comment, it.date) }
                    }

                    Spacer(Modifier.height(24.dp))
                    SectionTitle("Mi cuenta")
                    ProfileMenuItem(Icons.Default.Edit, "Editar perfil", onEditProfile)
                    ProfileMenuItem(Icons.Default.CreditCard, "Métodos de pago", onPaymentMethods)
                    ProfileMenuItem(Icons.Default.Inventory2, "Mis publicaciones", onMyListings)
                    ProfileMenuItem(Icons.Default.Settings, "Configuración", onSettings)
                    ProfileMenuItem(Icons.AutoMirrored.Filled.HelpOutline, "Ayuda y soporte", onHelp)

                    Spacer(Modifier.height(28.dp))
                    OutlinedButton(
                        onClick = { auth.logout(onLogout) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Cerrar sesión")
                    }
                }
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(14.dp))
        Text(title, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun StatChip(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(value, color = Color.White, fontWeight = FontWeight.Bold)
        Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
    }
}

@Composable
private fun ReviewCard(author: String, rating: Int, comment: String, date: String) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(author, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                repeat(rating) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(comment, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(date, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
