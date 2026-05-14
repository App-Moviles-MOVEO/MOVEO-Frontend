package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moveo_frontend.data.MockData
import com.example.moveo_frontend.ui.components.SectionTitle
import com.example.moveo_frontend.ui.components.VerifiedBadge

@Composable
fun ProfileScreen() {
    val user = MockData.currentUser
    val scroll = rememberScrollState()
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
                ) { Text(user.name.first().toString(), color = MaterialTheme.colorScheme.primary, fontSize = 36.sp, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.height(12.dp))
                Text(user.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(user.email, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatChip("${user.rating}★", "Rating")
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
            MockData.reviews.forEach { ReviewCard(it.author, it.rating, it.comment, it.date) }
        }
        Spacer(Modifier.height(80.dp))
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
