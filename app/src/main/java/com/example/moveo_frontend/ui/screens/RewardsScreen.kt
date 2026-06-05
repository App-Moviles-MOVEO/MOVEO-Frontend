package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.ui.components.WPBackButton
import com.example.moveo_frontend.ui.components.SectionTitle
import com.example.moveo_frontend.ui.theme.OrangeReward
import com.example.moveo_frontend.ui.viewmodel.ProfileViewModel
import com.example.moveo_frontend.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(onBack: () -> Unit) {
    val vm: ProfileViewModel = viewModel()
    val state by vm.user.collectAsState()
    val rewardPoints = (state as? UiState.Success)?.data?.rewardPoints ?: 0
    val scroll = rememberScrollState()
    Scaffold(topBar = {
        TopAppBar(title = { Text("Recompensas") }, navigationIcon = {
            WPBackButton(onClick = onBack)
        })
    }) { padding ->
        Column(Modifier.padding(padding).verticalScroll(scroll).padding(20.dp)) {
            Surface(
                color = OrangeReward,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Nivel Plata", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("$rewardPoints pts", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.45f },
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth().height(8.dp)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("550 pts más para llegar a Oro", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(20.dp))

            SectionTitle("Cómo ganar puntos")
            EarnRow("Completar un viaje", "+15 pts")
            EarnRow("Alquilar un vehículo", "+50 pts")
            EarnRow("Dejar reseña verificada", "+10 pts")
            EarnRow("Referir a un amigo", "+100 pts")

            Spacer(Modifier.height(20.dp))
            SectionTitle("Premios disponibles")
            RewardRow("S/ 5 OFF en tu próximo carpooling", 200)
            RewardRow("20% OFF en alquiler fin de semana", 400)
            RewardRow("Día gratis de alquiler", 1500)
        }
    }
}

@Composable
private fun EarnRow(label: String, points: String) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(label, modifier = Modifier.weight(1f))
            Text(points, color = OrangeReward, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RewardRow(label: String, cost: Int) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CardGiftcard, null, tint = OrangeReward)
            Spacer(Modifier.width(10.dp))
            Text(label, modifier = Modifier.weight(1f), fontSize = 14.sp)
            TextButton(onClick = {}) { Text("$cost pts") }
        }
    }
}
