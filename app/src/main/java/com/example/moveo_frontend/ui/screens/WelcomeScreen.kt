package com.example.moveo_frontend.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moveo_frontend.ui.components.BrandMark
import com.example.moveo_frontend.ui.theme.ManropeFontFamily
import kotlin.math.cos
import kotlin.math.sin

private val Blue = Color(0xFF3B82F6)
private val Black = Color(0xFF000000)
private val TextMuted = Black.copy(alpha = 0.62f)
private val ButtonText = Color(0xFFF6F6F6)

@Composable
fun WelcomeScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val angle by infiniteTransition.animateFloat(
        initialValue = 270f,
        targetValue = 630f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(52.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            BrandMark(
                size = 36.dp,
                background = Black,
                monogramColor = Color.White,
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "WheelsPe",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = ManropeFontFamily,
                color = Black,
                letterSpacing = (-0.54).sp
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            RadarAnimation(angle = angle)
        }

        Text(
            text = "Bienvenido a",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = ManropeFontFamily,
            color = TextMuted,
            letterSpacing = 0.sp
        )
        Spacer(Modifier.height(0.dp))
        Text(
            text = "WheelsPe.",
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = ManropeFontFamily,
            color = Black,
            lineHeight = 50.sp,
            letterSpacing = (-1.68).sp
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Tu auto, tu ruta, tu Perú.",
            fontSize = 17.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = ManropeFontFamily,
            color = TextMuted,
            lineHeight = 22.5.sp,
            letterSpacing = 0.sp
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onRegister,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Blue,
                contentColor = ButtonText
            )
        ) {
            Text(
                text = "Crear cuenta",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = ManropeFontFamily,
                color = ButtonText
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Black)
        ) {
            Text(
                text = "Ingresar",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = ManropeFontFamily,
                color = Black
            )
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun RadarAnimation(angle: Float) {
    val ringColor = Color(0xFFE5E7EB)
    val arcSweep = 70f

    Canvas(modifier = Modifier.size(260.dp)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val center = Offset(cx, cy)
        val r1 = size.width * 0.17f
        val r2 = size.width * 0.32f
        val r3 = size.width * 0.47f

        listOf(r1, r2, r3).forEach { r ->
            drawCircle(color = ringColor, radius = r, center = center, style = Stroke(width = 1.5.dp.toPx()))
        }

        drawArc(
            color = Blue.copy(alpha = 0.6f),
            startAngle = angle - arcSweep,
            sweepAngle = arcSweep,
            useCenter = false,
            topLeft = Offset(cx - r2, cy - r2),
            size = Size(r2 * 2, r2 * 2),
            style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
        )

        val rad = Math.toRadians(angle.toDouble())
        drawCircle(
            color = Blue,
            radius = 6.dp.toPx(),
            center = Offset(cx + (r2 * cos(rad)).toFloat(), cy + (r2 * sin(rad)).toFloat())
        )

        val staticRad = Math.toRadians(200.0)
        drawCircle(
            color = Color.Black,
            radius = 5.dp.toPx(),
            center = Offset(cx + (r1 * cos(staticRad)).toFloat(), cy + (r1 * sin(staticRad)).toFloat())
        )
    }
}
