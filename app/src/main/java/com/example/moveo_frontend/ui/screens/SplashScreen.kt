package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moveo_frontend.di.ServiceLocator
import com.example.moveo_frontend.ui.components.BrandMark
import com.example.moveo_frontend.ui.theme.BlueAccent
import com.example.moveo_frontend.ui.theme.BluePrimary
import kotlinx.coroutines.flow.first

@Composable
fun SplashScreen(onAuthed: () -> Unit, onUnauthed: () -> Unit) {
    LaunchedEffect(Unit) {
        // La sesión MOVEO es stateless: se identifica por el userId guardado, no por un token.
        val userId = ServiceLocator.session.userId.first()
        if (!userId.isNullOrBlank()) onAuthed() else onUnauthed()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BluePrimary, BlueAccent))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BrandMark(size = 104.dp, background = Color.White, monogramColor = BluePrimary)
            Spacer(Modifier.height(20.dp))
            Text("WheelsPe", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))
            CircularProgressIndicator(color = Color.White)
        }
    }
}
