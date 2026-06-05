package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.DirectionsCar
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
import com.example.moveo_frontend.data.Vehicle
import com.example.moveo_frontend.ui.components.RatingChip
import com.example.moveo_frontend.ui.components.SectionTitle
import com.example.moveo_frontend.ui.viewmodel.ProfileViewModel
import com.example.moveo_frontend.ui.viewmodel.UiState
import com.example.moveo_frontend.ui.viewmodel.VehiclesViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun HomeScreen(
    onCatalog: () -> Unit,
    onCarpoolSearch: () -> Unit,
    onSafety: () -> Unit,
    onRewards: () -> Unit,
    onNotifications: () -> Unit,
    onVehicleClick: (String) -> Unit
) {
    val vehiclesVm: VehiclesViewModel = viewModel()
    val profileVm: ProfileViewModel = viewModel()
    val vehiclesState by vehiclesVm.state.collectAsState()
    val userState by profileVm.user.collectAsState()
    val scroll = rememberScrollState()

    val lima = LatLng(-12.0464, -77.0428)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(lima, 12f)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scroll)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        val userName = (userState as? UiState.Success)?.data?.name ?: "Usuario"
        val rewardPoints = (userState as? UiState.Success)?.data?.rewardPoints ?: 0
        val firstName = userName.split(" ").first()

        Spacer(Modifier.height(14.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Avatar circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF3B82F6), androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = firstName.firstOrNull()?.toString().orEmpty(),
                    color = Color.White,
                    fontSize = 16.8.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = com.example.moveo_frontend.ui.theme.ManropeFontFamily,
                    letterSpacing = (-0.34).sp
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = "Hola,",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = com.example.moveo_frontend.ui.theme.ManropeFontFamily,
                    color = Color.Black.copy(alpha = 0.62f),
                    lineHeight = 12.sp
                )
                Text(
                    text = firstName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = com.example.moveo_frontend.ui.theme.ManropeFontFamily,
                    color = Color.Black,
                    letterSpacing = (-0.32).sp,
                    lineHeight = 16.sp
                )
            }

            // Notification button — SVG campana custom
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFE4E6E9), RoundedCornerShape(12.dp))
                    .clickable { onNotifications() },
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(20.dp)) {
                    val sw = 1.6.dp.toPx()
                    val scaleX = size.width / 20f
                    val scaleY = size.height / 20f
                    fun x(v: Float) = v * scaleX
                    fun y(v: Float) = v * scaleY

                    val bellBody = androidx.compose.ui.graphics.Path().apply {
                        moveTo(x(5f), y(13.3333f))
                        lineTo(x(5f), y(9.16663f))
                        cubicTo(x(5f), y(7.84054f), x(5.52678f), y(6.56877f), x(6.46447f), y(5.63109f))
                        cubicTo(x(7.40215f), y(4.69341f), x(8.67392f), y(4.16663f), x(10f), y(4.16663f))
                        cubicTo(x(11.3261f), y(4.16663f), x(12.5979f), y(4.69341f), x(13.5355f), y(5.63109f))
                        cubicTo(x(14.4732f), y(6.56877f), x(15f), y(7.84054f), x(15f), y(9.16663f))
                        lineTo(x(15f), y(13.3333f))
                        lineTo(x(16.25f), y(15f))
                        lineTo(x(3.75f), y(15f))
                        lineTo(x(5f), y(13.3333f))
                        close()
                    }
                    drawPath(bellBody, Color.Black, style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = sw,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round,
                        join = androidx.compose.ui.graphics.StrokeJoin.Round
                    ))

                    val bellBottom = androidx.compose.ui.graphics.Path().apply {
                        moveTo(x(8.33331f), y(17.5f))
                        cubicTo(x(8.33331f), y(17.942f), x(8.50891f), y(18.366f), x(8.82147f), y(18.6785f))
                        cubicTo(x(9.13403f), y(18.9911f), x(9.55795f), y(19.1667f), x(9.99998f), y(19.1667f))
                        cubicTo(x(10.442f), y(19.1667f), x(10.8659f), y(18.9911f), x(11.1785f), y(18.6785f))
                        cubicTo(x(11.4911f), y(18.366f), x(11.6666f), y(17.942f), x(11.6666f), y(17.5f))
                    }
                    drawPath(bellBottom, Color.Black, style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = sw,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round,
                        join = androidx.compose.ui.graphics.StrokeJoin.Round
                    ))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Card unificada: mapa + título + búsqueda
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            shadowElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                val vehicles = (vehiclesState as? UiState.Success)?.data.orEmpty()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Color.Gray, RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(mapType = MapType.NORMAL),
                        uiSettings = MapUiSettings(zoomControlsEnabled = false, mapToolbarEnabled = false)
                    ) {
                        Marker(state = MarkerState(position = lima), title = "Tú")
                        vehicles.take(8).forEachIndexed { index, _ ->
                            val offset = 0.01 * (index + 1)
                            Marker(state = MarkerState(position = LatLng(lima.latitude + offset, lima.longitude - offset)), title = "Auto disponible")
                        }
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                    Text(
                        text = "¿A dónde vamos?",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = com.example.moveo_frontend.ui.theme.ManropeFontFamily,
                        color = Color.Black,
                        letterSpacing = (-0.66).sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(Color(0xFFE4E6E9), RoundedCornerShape(14.dp))
                            .clickable(onClick = onCatalog),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.padding(start = 17.dp, end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.foundation.Canvas(modifier = Modifier.size(18.dp)) {
                                val sw = 1.6.dp.toPx()
                                val iconColor = androidx.compose.ui.graphics.Color(0x9E000000)
                                drawCircle(
                                    color = iconColor,
                                    radius = 5.25.dp.toPx(),
                                    center = androidx.compose.ui.geometry.Offset(8.25.dp.toPx(), 8.25.dp.toPx()),
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = sw, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                )
                                drawLine(
                                    color = iconColor,
                                    start = androidx.compose.ui.geometry.Offset(12.375.dp.toPx(), 12.375.dp.toPx()),
                                    end = androidx.compose.ui.geometry.Offset(15.dp.toPx(), 15.dp.toPx()),
                                    strokeWidth = sw,
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "Av. Javier Prado, San Isidro...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = com.example.moveo_frontend.ui.theme.ManropeFontFamily,
                                color = Color.Black.copy(alpha = 0.62f)
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))

        Text(
            text = "Accesos rápidos",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = com.example.moveo_frontend.ui.theme.ManropeFontFamily,
            color = Color.Black,
            letterSpacing = (-0.32).sp
        )

        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickAction("Alquilar auto", "Desde S/ 89/día", Modifier.weight(1f), onCatalog) { CarIcon() }
            QuickAction("Buscar ruta", "Comparte viaje", Modifier.weight(1f), onCarpoolSearch) { RouteIcon() }
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickAction("Contactos\nseguros", "Gestiona alertas", Modifier.weight(1f), onSafety) { ShieldIcon() }
            QuickAction("Recompensas", "$rewardPoints pts", Modifier.weight(1f), onRewards) { GiftIcon() }
        }
        Spacer(Modifier.height(20.dp))

        // Header "Cerca de ti" + "Ver todo"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cerca de ti",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = com.example.moveo_frontend.ui.theme.ManropeFontFamily,
                color = Color.Black,
                letterSpacing = (-0.32).sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Ver todo",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = com.example.moveo_frontend.ui.theme.ManropeFontFamily,
                color = Color(0xFF3B82F6),
                modifier = Modifier.clickable { onCatalog() }
            )
        }
        Spacer(Modifier.height(12.dp))

        when (val s = vehiclesState) {
            is UiState.Loading -> CircularProgressIndicator(Modifier.padding(16.dp))
            is UiState.Error -> Text(s.message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            is UiState.Success -> {
                if (s.data.isEmpty()) {
                    Text("Sin vehículos disponibles", color = Color.Black.copy(alpha = 0.62f), fontSize = 13.sp)
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(end = 20.dp)
                    ) {
                        items(s.data) { v ->
                            NearbyCard(v) { onVehicleClick(v.id) }
                        }
                    }
                }
            }
            UiState.Idle -> Unit
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun QuickAction(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            Modifier.padding(start = 15.dp, top = 15.dp, bottom = 14.dp, end = 15.dp)
        ) {
            Box(
                Modifier
                    .size(36.dp)
                    .background(Color(0xFF3B82F6).copy(alpha = 0.14f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = com.example.moveo_frontend.ui.theme.ManropeFontFamily,
                color = Color.Black,
                letterSpacing = (-0.14).sp,
                lineHeight = 14.sp
            )
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = com.example.moveo_frontend.ui.theme.ManropeFontFamily,
                color = Color.Black.copy(alpha = 0.62f),
                lineHeight = 11.5.sp
            )
        }
    }
}

@Composable
private fun CarIcon() {
    val blue = Color(0xFF3B82F6)
    val sw = 1.6f
    androidx.compose.foundation.Canvas(modifier = Modifier.size(18.dp)) {
        val scale = size.width / 18f
        fun sx(v: Float) = v * scale
        fun sy(v: Float) = v * scale

        val strokeStyle = androidx.compose.ui.graphics.drawscope.Stroke(
            width = sw * scale,
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
            join = androidx.compose.ui.graphics.StrokeJoin.Round
        )

        val body = androidx.compose.ui.graphics.Path().apply {
            moveTo(sx(4.5f), sy(12.75f)); lineTo(sx(4.5f), sy(14.25f))
            moveTo(sx(13.5f), sy(12.75f)); lineTo(sx(13.5f), sy(14.25f))
            moveTo(sx(3.75f), sy(9.75f))
            lineTo(sx(4.875f), sy(6f))
            cubicTo(sx(4.96f), sy(5.67f), sx(5.155f), sy(5.379f), sx(5.428f), sy(5.174f))
            cubicTo(sx(5.7f), sy(4.97f), sx(6.034f), sy(4.864f), sx(6.375f), sy(4.875f))
            lineTo(sx(11.625f), sy(4.875f))
            cubicTo(sx(11.966f), sy(4.864f), sx(12.3f), sy(4.97f), sx(12.572f), sy(5.174f))
            cubicTo(sx(12.845f), sy(5.379f), sx(13.04f), sy(5.67f), sx(13.125f), sy(6f))
            lineTo(sx(14.25f), sy(9.75f))
            moveTo(sx(3.75f), sy(12.75f))
            lineTo(sx(14.25f), sy(12.75f))
            cubicTo(sx(14.449f), sy(12.75f), sx(14.64f), sy(12.671f), sx(14.78f), sy(12.53f))
            cubicTo(sx(14.921f), sy(12.39f), sx(15f), sy(12.199f), sx(15f), sy(12f))
            lineTo(sx(15f), sy(9.75f))
            lineTo(sx(3f), sy(9.75f))
            lineTo(sx(3f), sy(12f))
            cubicTo(sx(3f), sy(12.199f), sx(3.079f), sy(12.39f), sx(3.22f), sy(12.53f))
            cubicTo(sx(3.36f), sy(12.671f), sx(3.551f), sy(12.75f), sx(3.75f), sy(12.75f))
            close()
        }
        drawPath(body, blue, style = strokeStyle)

        val leftWheel = androidx.compose.ui.graphics.Path().apply {
            addOval(androidx.compose.ui.geometry.Rect(sx(5.25f), sy(10.5f), sx(6.75f), sy(12f)))
        }
        drawPath(leftWheel, blue, style = strokeStyle)

        val rightWheel = androidx.compose.ui.graphics.Path().apply {
            addOval(androidx.compose.ui.geometry.Rect(sx(11.25f), sy(10.5f), sx(12.75f), sy(12f)))
        }
        drawPath(rightWheel, blue, style = strokeStyle)
    }
}

@Composable
private fun RouteIcon() {
    val blue = Color(0xFF3B82F6)
    val sw = 1.6f
    androidx.compose.foundation.Canvas(Modifier.size(18.dp)) {
        val sc = size.width / 18f
        val style = androidx.compose.ui.graphics.drawscope.Stroke(sw * sc, cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
        // Circle top-left
        drawCircle(blue, 1.5f * sc, androidx.compose.ui.geometry.Offset(4.5f * sc, 4.5f * sc), style = style)
        // Circle bottom-right
        drawCircle(blue, 1.5f * sc, androidx.compose.ui.geometry.Offset(13.5f * sc, 13.5f * sc), style = style)
        // S-curve path
        val p = androidx.compose.ui.graphics.Path().apply {
            moveTo(6f * sc, 4.5f * sc)
            lineTo(10.5f * sc, 4.5f * sc)
            cubicTo(11.296f * sc, 4.5f * sc, 12.059f * sc, 4.816f * sc, 12.621f * sc, 5.379f * sc)
            cubicTo(13.184f * sc, 5.941f * sc, 13.5f * sc, 6.704f * sc, 13.5f * sc, 7.5f * sc)
            cubicTo(13.5f * sc, 8.296f * sc, 13.184f * sc, 9.059f * sc, 12.621f * sc, 9.621f * sc)
            cubicTo(12.059f * sc, 10.184f * sc, 11.296f * sc, 10.5f * sc, 10.5f * sc, 10.5f * sc)
            lineTo(7.5f * sc, 10.5f * sc)
            cubicTo(6.704f * sc, 10.5f * sc, 5.941f * sc, 10.816f * sc, 5.379f * sc, 11.379f * sc)
            cubicTo(4.816f * sc, 11.941f * sc, 4.5f * sc, 12.704f * sc, 4.5f * sc, 13.5f * sc)
        }
        drawPath(p, blue, style = style)
    }
}

@Composable
private fun ShieldIcon() {
    val blue = Color(0xFF3B82F6)
    val sw = 1.6f
    androidx.compose.foundation.Canvas(Modifier.size(18.dp)) {
        val sc = size.width / 18f
        val style = androidx.compose.ui.graphics.drawscope.Stroke(sw * sc, cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
        val shield = androidx.compose.ui.graphics.Path().apply {
            moveTo(9f * sc, 2.25f * sc)
            lineTo(15f * sc, 4.5f * sc)
            lineTo(15f * sc, 9f * sc)
            cubicTo(15f * sc, 12.75f * sc, 12.375f * sc, 15f * sc, 9f * sc, 15.75f * sc)
            cubicTo(5.625f * sc, 15f * sc, 3f * sc, 12.75f * sc, 3f * sc, 9f * sc)
            lineTo(3f * sc, 4.5f * sc)
            close()
        }
        drawPath(shield, blue, style = style)
        val check = androidx.compose.ui.graphics.Path().apply {
            moveTo(6.75f * sc, 9f * sc)
            lineTo(8.25f * sc, 10.5f * sc)
            lineTo(11.25f * sc, 7.5f * sc)
        }
        drawPath(check, blue, style = style)
    }
}

@Composable
private fun GiftIcon() {
    val blue = Color(0xFF3B82F6)
    val sw = 1.6f
    androidx.compose.foundation.Canvas(Modifier.size(18.dp)) {
        val sc = size.width / 18f
        val style = androidx.compose.ui.graphics.drawscope.Stroke(sw * sc, cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
        // Gift box rectangle
        val box = androidx.compose.ui.graphics.Path().apply {
            moveTo(14.25f * sc, 6.75f * sc)
            lineTo(3.75f * sc, 6.75f * sc)
            cubicTo(2.922f * sc, 6.75f * sc, 2.25f * sc, 7.422f * sc, 2.25f * sc, 8.25f * sc)
            lineTo(2.25f * sc, 13.5f * sc)
            cubicTo(2.25f * sc, 14.328f * sc, 2.922f * sc, 15f * sc, 3.75f * sc, 15f * sc)
            lineTo(14.25f * sc, 15f * sc)
            cubicTo(15.078f * sc, 15f * sc, 15.75f * sc, 14.328f * sc, 15.75f * sc, 13.5f * sc)
            lineTo(15.75f * sc, 8.25f * sc)
            cubicTo(15.75f * sc, 7.422f * sc, 15.078f * sc, 6.75f * sc, 14.25f * sc, 6.75f * sc)
            close()
        }
        drawPath(box, blue, style = style)
        // Horizontal divider
        drawLine(blue, androidx.compose.ui.geometry.Offset(2.25f * sc, 9.75f * sc), androidx.compose.ui.geometry.Offset(15.75f * sc, 9.75f * sc), sw * sc, androidx.compose.ui.graphics.StrokeCap.Round)
        // Vertical divider
        drawLine(blue, androidx.compose.ui.geometry.Offset(9f * sc, 6.75f * sc), androidx.compose.ui.geometry.Offset(9f * sc, 15f * sc), sw * sc, androidx.compose.ui.graphics.StrokeCap.Round)
        // Bow left
        val bow = androidx.compose.ui.graphics.Path().apply {
            moveTo(6f * sc, 6.75f * sc)
            cubicTo(4.5f * sc, 6.75f * sc, 3.75f * sc, 4.5f * sc, 5.25f * sc, 3.75f * sc)
            cubicTo(6.75f * sc, 3f * sc, 8.25f * sc, 6.75f * sc, 8.25f * sc, 6.75f * sc)
        }
        drawPath(bow, blue, style = style)
    }
}

@Composable
private fun NearbyCard(v: Vehicle, onClick: () -> Unit) {
    val blue = Color(0xFF3B82F6)
    val muted = Color.Black.copy(alpha = 0.62f)
    val manrope = com.example.moveo_frontend.ui.theme.ManropeFontFamily

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 0.dp,
        modifier = Modifier.width(226.dp).clickable(onClick = onClick)
    ) {
        Column {
            // Image area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.DirectionsCar, null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(44.dp))
            }

            Column(modifier = Modifier.padding(start = 13.dp, end = 11.dp, top = 10.dp, bottom = 12.dp)) {
                // Title + badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${v.brand} ${v.model}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = manrope,
                        color = Color.Black,
                        letterSpacing = (-0.14).sp,
                        lineHeight = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .background(blue, RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Cerca",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = manrope,
                            color = Color(0xFFF6F6F6)
                        )
                    }
                }

                // Subtitle
                Text(
                    text = "${v.type} · ${v.transmission}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = manrope,
                    color = muted,
                    lineHeight = 11.sp
                )

                Spacer(Modifier.height(10.dp))

                // Price + rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "S/${v.pricePerDay}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = manrope,
                            color = Color.Black,
                            letterSpacing = (-0.32).sp
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = "/día",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = manrope,
                            color = muted,
                            modifier = Modifier.padding(bottom = 1.dp)
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    // Star filled + rating
                    androidx.compose.foundation.Canvas(Modifier.size(11.dp)) {
                        val path = androidx.compose.ui.graphics.Path().apply {
                            val sc = size.width / 11f
                            moveTo(5.5f * sc, 1.375f * sc)
                            lineTo(6.692f * sc, 3.942f * sc)
                            lineTo(9.442f * sc, 4.308f * sc)
                            lineTo(7.425f * sc, 6.233f * sc)
                            lineTo(7.929f * sc, 8.983f * sc)
                            lineTo(5.5f * sc, 7.7f * sc)
                            lineTo(3.071f * sc, 8.983f * sc)
                            lineTo(3.575f * sc, 6.233f * sc)
                            lineTo(1.558f * sc, 4.308f * sc)
                            lineTo(4.308f * sc, 3.942f * sc)
                            close()
                        }
                        drawPath(path, blue)
                    }
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = "${v.rating}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = manrope,
                        color = muted
                    )
                }
            }
        }
    }
}
