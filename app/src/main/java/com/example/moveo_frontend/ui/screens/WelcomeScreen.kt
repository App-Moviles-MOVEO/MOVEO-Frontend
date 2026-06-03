package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moveo_frontend.ui.components.BrandMark
import com.example.moveo_frontend.ui.components.WPButton
import com.example.moveo_frontend.ui.components.WPOutlinedButton
import com.example.moveo_frontend.ui.theme.BlueAccent
import com.example.moveo_frontend.ui.theme.BluePrimary

@Composable
fun WelcomeScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BluePrimary, BlueAccent)))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(80.dp))
        BrandMark(
            size = 112.dp,
            background = androidx.compose.ui.graphics.Color.White,
            monogramColor = BluePrimary
        )
        Spacer(Modifier.height(28.dp))
        Text(
            "WheelsPe",
            color = androidx.compose.ui.graphics.Color.White,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Alquila, comparte y muévete\nde la mejor forma.",
            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
        Spacer(Modifier.weight(1f))
        Surface(
            color = androidx.compose.ui.graphics.Color.White,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp)) {
                WPButton("Crear cuenta", onClick = onRegister)
                Spacer(Modifier.height(12.dp))
                WPOutlinedButton("Ya tengo cuenta", onClick = onLogin)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Al continuar aceptas los Términos y la Política de Privacidad de WheelsPe",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}
