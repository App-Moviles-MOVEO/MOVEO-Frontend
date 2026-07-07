package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moveo_frontend.ui.theme.ManropeFontFamily

private val NavActive   = Color(0xFF000000)
private val NavInactive = Color(0x6B0A0A0A)

@Composable
fun MainScreen(
    onCatalog: () -> Unit,
    onCarpoolSearch: () -> Unit,
    onCarpoolDetail: (String) -> Unit,
    onSafety: () -> Unit,
    onRewards: () -> Unit,
    onNotifications: () -> Unit,
    onVehicleClick: (String) -> Unit,
    onReservationClick: (String) -> Unit,
    onCarpoolTrack: (String) -> Unit,
    onPublishVehicle: () -> Unit,
    onPublishCarpool: () -> Unit,
    onEditProfile: () -> Unit,
    onPaymentMethods: () -> Unit,
    onMyListings: () -> Unit,
    onSettings: () -> Unit,
    onHelp: () -> Unit,
    onLogout: () -> Unit
) {
    var tab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = { CustomNavBar(selected = tab, onSelect = { tab = it }) }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (tab) {
                0 -> HomeScreen(onCatalog, onCarpoolSearch, onSafety, onRewards, onNotifications, onVehicleClick)
                1 -> CatalogScreen(onBack = { tab = 0 }, onVehicleClick = onVehicleClick, onPublish = onPublishVehicle)
                2 -> CarpoolSearchScreen(onBack = { tab = 0 }, onRouteClick = onCarpoolDetail, onPublish = onPublishCarpool)
                3 -> ReservationsScreen(onClick = onReservationClick, onCarpoolClick = onCarpoolTrack)
                4 -> ProfileScreen(onLogout = onLogout, onEditProfile = onEditProfile, onPaymentMethods = onPaymentMethods, onMyListings = onMyListings, onSettings = onSettings, onHelp = onHelp)
            }
        }
    }
}

@Composable
private fun CustomNavBar(selected: Int, onSelect: (Int) -> Unit) {
    val items = listOf("Inicio", "Catálogo", "Carpool", "Reservas", "Perfil")
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEachIndexed { i, label ->
                val active = selected == i
                val tint = if (active) NavActive else NavInactive
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onSelect(i) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (i) {
                        0 -> NavHomeIcon(tint)
                        1 -> NavCatalogIcon(tint)
                        2 -> NavCarpoolIcon(tint)
                        3 -> NavReservasIcon(tint)
                        4 -> NavProfileIcon(tint)
                    }
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = label,
                        fontSize = 10.5.sp,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = ManropeFontFamily,
                        color = tint,
                        letterSpacing = (-0.105).sp
                    )
                }
            }
        }
    }
}

@Composable
private fun NavHomeIcon(tint: Color) {
    Canvas(Modifier.size(22.dp)) {
        val sc = size.width / 22f
        val style = Stroke(1.8f * sc, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val path = Path().apply {
            moveTo(3.667f * sc, 10.083f * sc); lineTo(11f * sc, 3.667f * sc); lineTo(18.333f * sc, 10.083f * sc)
            lineTo(18.333f * sc, 18.333f * sc)
            cubicTo(18.333f * sc, 18.577f * sc, 18.237f * sc, 18.81f * sc, 18.065f * sc, 18.982f * sc)
            cubicTo(17.893f * sc, 19.153f * sc, 17.66f * sc, 19.25f * sc, 17.417f * sc, 19.25f * sc)
            lineTo(13.75f * sc, 19.25f * sc); lineTo(13.75f * sc, 13.75f * sc)
            lineTo(8.25f * sc, 13.75f * sc); lineTo(8.25f * sc, 19.25f * sc)
            lineTo(4.583f * sc, 19.25f * sc)
            cubicTo(4.34f * sc, 19.25f * sc, 4.107f * sc, 19.153f * sc, 3.935f * sc, 18.982f * sc)
            cubicTo(3.763f * sc, 18.81f * sc, 3.667f * sc, 18.577f * sc, 3.667f * sc, 18.333f * sc)
            close()
        }
        drawPath(path, tint, style = style)
    }
}

@Composable
private fun NavCatalogIcon(tint: Color) {
    Canvas(Modifier.size(22.dp)) {
        val sc = size.width / 22f
        val style = Stroke(1.6f * sc, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val car = Path().apply {
            moveTo(4.5f * sc, 14.667f * sc); lineTo(4.5f * sc, 16f * sc)
            moveTo(17.5f * sc, 14.667f * sc); lineTo(17.5f * sc, 16f * sc)
            moveTo(4f * sc, 11f * sc); lineTo(5.25f * sc, 7f * sc)
            cubicTo(5.36f * sc, 6.6f * sc, 5.595f * sc, 6.24f * sc, 5.92f * sc, 5.98f * sc)
            cubicTo(6.245f * sc, 5.72f * sc, 6.645f * sc, 5.58f * sc, 7.05f * sc, 5.58f * sc)
            lineTo(14.95f * sc, 5.58f * sc)
            cubicTo(15.355f * sc, 5.58f * sc, 15.755f * sc, 5.72f * sc, 16.08f * sc, 5.98f * sc)
            cubicTo(16.405f * sc, 6.24f * sc, 16.64f * sc, 6.6f * sc, 16.75f * sc, 7f * sc)
            lineTo(18f * sc, 11f * sc)
            moveTo(4f * sc, 14.667f * sc); lineTo(18f * sc, 14.667f * sc)
            cubicTo(18.245f * sc, 14.667f * sc, 18.48f * sc, 14.57f * sc, 18.652f * sc, 14.398f * sc)
            cubicTo(18.824f * sc, 14.226f * sc, 18.917f * sc, 13.991f * sc, 18.917f * sc, 13.75f * sc)
            lineTo(18.917f * sc, 11f * sc); lineTo(3.083f * sc, 11f * sc)
            lineTo(3.083f * sc, 13.75f * sc)
            cubicTo(3.083f * sc, 13.991f * sc, 3.176f * sc, 14.226f * sc, 3.348f * sc, 14.398f * sc)
            cubicTo(3.52f * sc, 14.57f * sc, 3.755f * sc, 14.667f * sc, 4f * sc, 14.667f * sc)
            close()
        }
        drawPath(car, tint, style = style)
        drawCircle(tint, 1.4f * sc, Offset(7f * sc, 12.5f * sc), style = style)
        drawCircle(tint, 1.4f * sc, Offset(15f * sc, 12.5f * sc), style = style)
    }
}

@Composable
private fun NavCarpoolIcon(tint: Color) {
    Canvas(Modifier.size(22.dp)) {
        val sc = size.width / 22f
        val style = Stroke(1.6f * sc, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(tint, 1.833f * sc, Offset(5.5f * sc, 5.5f * sc), style = style)
        drawCircle(tint, 1.833f * sc, Offset(16.5f * sc, 16.5f * sc), style = style)
        val curve = Path().apply {
            moveTo(7.333f * sc, 5.5f * sc); lineTo(12.833f * sc, 5.5f * sc)
            cubicTo(13.806f * sc, 5.5f * sc, 14.738f * sc, 5.886f * sc, 15.426f * sc, 6.574f * sc)
            cubicTo(16.114f * sc, 7.262f * sc, 16.5f * sc, 8.194f * sc, 16.5f * sc, 9.167f * sc)
            cubicTo(16.5f * sc, 10.139f * sc, 16.114f * sc, 11.072f * sc, 15.426f * sc, 11.759f * sc)
            cubicTo(14.738f * sc, 12.447f * sc, 13.806f * sc, 12.833f * sc, 12.833f * sc, 12.833f * sc)
            lineTo(9.167f * sc, 12.833f * sc)
            cubicTo(8.194f * sc, 12.833f * sc, 7.262f * sc, 13.22f * sc, 6.574f * sc, 13.907f * sc)
            cubicTo(5.886f * sc, 14.595f * sc, 5.5f * sc, 15.528f * sc, 5.5f * sc, 16.5f * sc)
        }
        drawPath(curve, tint, style = style)
    }
}

@Composable
private fun NavReservasIcon(tint: Color) {
    Canvas(Modifier.size(22.dp)) {
        val sc = size.width / 22f
        val style = Stroke(1.6f * sc, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val cal = Path().apply {
            moveTo(6.417f * sc, 3.667f * sc); lineTo(6.417f * sc, 6.417f * sc)
            moveTo(15.583f * sc, 3.667f * sc); lineTo(15.583f * sc, 6.417f * sc)
            moveTo(3.667f * sc, 9.167f * sc); lineTo(18.333f * sc, 9.167f * sc)
            moveTo(4.583f * sc, 5.5f * sc); lineTo(17.417f * sc, 5.5f * sc)
            cubicTo(17.66f * sc, 5.5f * sc, 17.893f * sc, 5.597f * sc, 18.065f * sc, 5.768f * sc)
            cubicTo(18.237f * sc, 5.94f * sc, 18.333f * sc, 6.173f * sc, 18.333f * sc, 6.417f * sc)
            lineTo(18.333f * sc, 17.417f * sc)
            cubicTo(18.333f * sc, 17.66f * sc, 18.237f * sc, 17.893f * sc, 18.065f * sc, 18.065f * sc)
            cubicTo(17.893f * sc, 18.237f * sc, 17.66f * sc, 18.333f * sc, 17.417f * sc, 18.333f * sc)
            lineTo(4.583f * sc, 18.333f * sc)
            cubicTo(4.34f * sc, 18.333f * sc, 4.107f * sc, 18.237f * sc, 3.935f * sc, 18.065f * sc)
            cubicTo(3.763f * sc, 17.893f * sc, 3.667f * sc, 17.66f * sc, 3.667f * sc, 17.417f * sc)
            lineTo(3.667f * sc, 6.417f * sc)
            cubicTo(3.667f * sc, 6.173f * sc, 3.763f * sc, 5.94f * sc, 3.935f * sc, 5.768f * sc)
            cubicTo(4.107f * sc, 5.597f * sc, 4.34f * sc, 5.5f * sc, 4.583f * sc, 5.5f * sc)
            close()
        }
        drawPath(cal, tint, style = style)
    }
}

@Composable
private fun NavProfileIcon(tint: Color) {
    Canvas(Modifier.size(22.dp)) {
        val sc = size.width / 22f
        val style = Stroke(1.6f * sc, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(tint, 3.667f * sc, Offset(11f * sc, 7.333f * sc), style = style)
        val body = Path().apply {
            moveTo(3.667f * sc, 18.333f * sc)
            cubicTo(3.667f * sc, 16.388f * sc, 4.439f * sc, 14.523f * sc, 5.815f * sc, 13.148f * sc)
            cubicTo(7.19f * sc, 11.773f * sc, 9.055f * sc, 11f * sc, 11f * sc, 11f * sc)
            cubicTo(12.945f * sc, 11f * sc, 14.81f * sc, 11.773f * sc, 16.185f * sc, 13.148f * sc)
            cubicTo(17.561f * sc, 14.523f * sc, 18.333f * sc, 16.388f * sc, 18.333f * sc, 18.333f * sc)
        }
        drawPath(body, tint, style = style)
    }
}
