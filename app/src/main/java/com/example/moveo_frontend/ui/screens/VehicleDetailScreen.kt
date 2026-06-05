package com.example.moveo_frontend.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.ui.components.WPBackButton
import com.example.moveo_frontend.ui.components.RatingChip
import com.example.moveo_frontend.ui.components.StateContainer
import com.example.moveo_frontend.ui.components.VerifiedBadge
import com.example.moveo_frontend.ui.theme.ManropeFontFamily
import com.example.moveo_frontend.ui.viewmodel.VehicleDetailViewModel
import kotlin.math.cos
import kotlin.math.sin

private val VdBackground  = Color(0xFFF6F6F6)
private val VdWhite       = Color(0xFFFFFFFF)
private val VdBlack       = Color(0xFF000000)
private val VdMuted       = VdBlack.copy(alpha = 0.62f)
private val VdBlue        = Color(0xFF3B82F6)
private val VdDeposit     = 200

@Composable
fun VehicleDetailScreen(
    id: String,
    onBack: () -> Unit,
    onReserve: (String) -> Unit,
    onChat: (String) -> Unit
) {
    val vm: VehicleDetailViewModel = viewModel()
    val state by vm.state.collectAsState()
    LaunchedEffect(id) { vm.load(id) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VdBackground)
    ) {
        StateContainer(state, onRetry = { vm.load(id) }) { v ->
            val scroll = rememberScrollState()
            var isFav by remember { mutableStateOf(false) }
            val photoCount = 3
            val currentPhoto = 0

            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scroll)
                ) {
                    // Photo section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        // Photo placeholder
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color(0xFFE8E8E8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.DirectionsCar,
                                contentDescription = null,
                                tint = Color(0xFFBBBBBB),
                                modifier = Modifier.size(96.dp)
                            )
                        }

                        // Back + Fav buttons row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Back button
                            WPBackButton(onClick = onBack)

                            // Favorite button
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(VdWhite, RoundedCornerShape(12.dp))
                                    .clickable { isFav = !isFav },
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.foundation.Canvas(
                                    modifier = Modifier.size(width = 14.33.dp, height = 13.33.dp)
                                ) {
                                    val cx = size.width / 2f
                                    val cy = size.height / 2f
                                    val sw = 1.6.dp.toPx()
                                    val outerR = (size.height / 2f) - sw / 2f
                                    val innerR = outerR * 0.382f
                                    val path = Path()
                                    for (i in 0 until 10) {
                                        val angle = Math.toRadians((-90 + i * 36).toDouble())
                                        val r = if (i % 2 == 0) outerR else innerR
                                        val x = cx + (r * cos(angle)).toFloat()
                                        val y = cy + (r * sin(angle)).toFloat()
                                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                    }
                                    path.close()
                                    drawPath(
                                        path,
                                        if (isFav) VdBlue else VdBlack,
                                        style = Stroke(sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                    )
                                }
                            }
                        }

                        // Dots indicator
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(photoCount) { i ->
                                val w by animateDpAsState(if (i == currentPhoto) 18.dp else 6.dp, label = "dot")
                                Box(
                                    modifier = Modifier
                                        .width(w)
                                        .height(6.dp)
                                        .background(
                                            if (i == currentPhoto) VdBlue else VdWhite,
                                            RoundedCornerShape(3.dp)
                                        )
                                )
                            }
                        }
                    }

                    // Content
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Spacer(Modifier.height(20.dp))

                        // Title + Price row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Left: title + subtitle
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${v.brand} ${v.model}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = ManropeFontFamily,
                                    color = VdBlack,
                                    letterSpacing = (-0.72).sp
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "${v.type} · ${v.transmission} · ${v.year}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontFamily = ManropeFontFamily,
                                    color = VdMuted
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            // Right: price + guarantee
                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "S/${v.pricePerDay}",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = ManropeFontFamily,
                                        color = VdBlack,
                                        letterSpacing = (-0.72).sp
                                    )
                                    Spacer(Modifier.width(2.dp))
                                    Text(
                                        text = "/día",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontFamily = ManropeFontFamily,
                                        color = VdMuted,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }
                                Text(
                                    text = "+ S/$VdDeposit garantía",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontFamily = ManropeFontFamily,
                                    color = VdMuted
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Specs — tarjeta blanca
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(VdWhite, RoundedCornerShape(12.dp))
                                .padding(vertical = 16.dp)
                        ) {
                            Spec(Icons.Default.AirlineSeatReclineNormal, "${v.seats} plazas")
                            Spec(Icons.Default.LocalGasStation, v.fuel)
                            Spec(Icons.Default.Settings, v.transmission)
                            Spec(Icons.Default.AcUnit, "A/C")
                        }

                        Spacer(Modifier.height(16.dp))

                        // Owner
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(VdWhite, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Box(
                                Modifier
                                    .size(44.dp)
                                    .background(VdBlue, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    v.ownerName.first().toString(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = ManropeFontFamily
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(v.ownerName, fontWeight = FontWeight.SemiBold, fontFamily = ManropeFontFamily)
                                    Spacer(Modifier.width(4.dp))
                                    if (v.ownerVerified) VerifiedBadge()
                                }
                                Text(
                                    "Propietario · ${v.rating} ★",
                                    fontSize = 12.sp,
                                    color = VdMuted,
                                    fontFamily = ManropeFontFamily
                                )
                            }
                            IconButton(onClick = { onChat(v.ownerId.toString()) }) {
                                Icon(Icons.Default.Chat, null, tint = VdBlack)
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            "Sobre este auto",
                            fontWeight = FontWeight.Bold,
                            fontFamily = ManropeFontFamily,
                            fontSize = 16.sp,
                            color = VdBlack
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            v.description,
                            lineHeight = 20.sp,
                            color = VdMuted,
                            fontFamily = ManropeFontFamily,
                            fontSize = 14.sp
                        )

                        Spacer(Modifier.height(16.dp))

                        Surface(color = Color(0xFFEEF4FF), shape = RoundedCornerShape(12.dp)) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Shield, null, tint = VdBlue)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "Garantía retenida en escrow",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        fontFamily = ManropeFontFamily
                                    )
                                    Text(
                                        "Tu dinero se libera al propietario tras la devolución",
                                        fontSize = 11.sp,
                                        color = VdMuted,
                                        fontFamily = ManropeFontFamily
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                    }
                }

                // Bottom bar
                Surface(color = VdWhite, shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                            .navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Total · 2 días",
                                fontSize = 12.sp,
                                fontFamily = ManropeFontFamily,
                                color = VdMuted
                            )
                            Text(
                                "S/ ${v.pricePerDay * 2} + S/$VdDeposit",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = ManropeFontFamily,
                                color = VdBlack
                            )
                        }
                        Button(
                            onClick = { onReserve(v.id) },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = VdBlue),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                "Reservar",
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = ManropeFontFamily,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Spec(icon: ImageVector, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = VdBlue, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            fontFamily = ManropeFontFamily,
            color = VdBlack
        )
    }
}
