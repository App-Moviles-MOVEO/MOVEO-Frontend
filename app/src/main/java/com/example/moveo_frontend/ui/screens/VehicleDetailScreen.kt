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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.moveo_frontend.data.session.RentalDateStore
import com.example.moveo_frontend.ui.components.WPBackButton
import com.example.moveo_frontend.ui.components.RatingChip
import com.example.moveo_frontend.ui.components.StateContainer
import com.example.moveo_frontend.ui.components.VerifiedBadge
import com.example.moveo_frontend.ui.theme.BlueAccent
import com.example.moveo_frontend.ui.theme.GrayBackground
import com.example.moveo_frontend.ui.theme.GraySurface
import com.example.moveo_frontend.ui.theme.ManropeFontFamily
import com.example.moveo_frontend.ui.theme.TextDark
import com.example.moveo_frontend.ui.theme.TextMuted
import com.example.moveo_frontend.ui.viewmodel.VehicleDetailViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VehicleDetailScreen(
    id: String,
    onBack: () -> Unit,
    onReserve: (String) -> Unit,
    onChat: (String) -> Unit
) {
    val vm: VehicleDetailViewModel = viewModel()
    val state by vm.state.collectAsState()
    val reviews by vm.reviews.collectAsState()
    LaunchedEffect(id) { vm.load(id) }

    // Fechas elegidas en el catálogo (si no hay, se asumen 2 días y se eligen en el pago).
    val startSel by RentalDateStore.startMillis.collectAsState()
    val endSel by RentalDateStore.endMillis.collectAsState()
    val days = remember(startSel, endSel) { RentalDateStore.days(default = 2) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        if (v.imageUrl != null) {
                            AsyncImage(
                                model = v.imageUrl,
                                contentDescription = "${v.brand} ${v.model}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
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
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            WPBackButton(onClick = onBack)

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(GraySurface, RoundedCornerShape(12.dp))
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
                                        if (isFav) BlueAccent else TextDark,
                                        style = Stroke(sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                    )
                                }
                            }
                        }

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
                                            if (i == currentPhoto) BlueAccent else GraySurface,
                                            RoundedCornerShape(3.dp)
                                        )
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Spacer(Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${v.brand} ${v.model}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = ManropeFontFamily,
                                    color = TextDark,
                                    letterSpacing = (-0.72).sp
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "${v.type} · ${v.transmission} · ${v.year}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontFamily = ManropeFontFamily,
                                    color = TextMuted
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "S/${v.pricePerDay}",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = ManropeFontFamily,
                                        color = TextDark,
                                        letterSpacing = (-0.72).sp
                                    )
                                    Spacer(Modifier.width(2.dp))
                                    Text(
                                        text = "/día",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontFamily = ManropeFontFamily,
                                        color = TextMuted,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }
                                Text(
                                    text = "+ S/${v.depositAmount} garantía",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontFamily = ManropeFontFamily,
                                    color = TextMuted
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GraySurface, RoundedCornerShape(12.dp))
                                .padding(vertical = 16.dp)
                        ) {
                            Spec(Icons.Default.AirlineSeatReclineNormal, "${v.seats} plazas")
                            Spec(Icons.Default.LocalGasStation, v.fuel)
                            Spec(Icons.Default.Settings, v.transmission)
                            Spec(Icons.Default.AcUnit, "A/C")
                        }

                        Spacer(Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GraySurface, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Box(
                                Modifier
                                    .size(44.dp)
                                    .background(BlueAccent, CircleShape),
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
                                    if (v.reviewsCount > 0)
                                        "Propietario · ${v.rating} ★ · ${v.reviewsCount} reseñas"
                                    else "Propietario · ${v.rating} ★",
                                    fontSize = 12.sp,
                                    color = TextMuted,
                                    fontFamily = ManropeFontFamily
                                )
                            }
                            IconButton(onClick = { onChat(v.ownerId.toString()) }) {
                                Icon(Icons.Default.Chat, null, tint = TextDark)
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            "Sobre este auto",
                            fontWeight = FontWeight.Bold,
                            fontFamily = ManropeFontFamily,
                            fontSize = 16.sp,
                            color = TextDark
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            v.description,
                            lineHeight = 20.sp,
                            color = TextMuted,
                            fontFamily = ManropeFontFamily,
                            fontSize = 14.sp
                        )

                        Spacer(Modifier.height(16.dp))

                        Surface(color = Color(0xFFEEF4FF), shape = RoundedCornerShape(12.dp)) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Shield, null, tint = BlueAccent)
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
                                        color = TextMuted,
                                        fontFamily = ManropeFontFamily
                                    )
                                }
                            }
                        }

                        // Reseñas reales del vehículo (calificaciones de otros arrendatarios).
                        if (reviews.isNotEmpty()) {
                            Spacer(Modifier.height(20.dp))
                            Text(
                                "Reseñas (${reviews.size})",
                                fontWeight = FontWeight.Bold,
                                fontFamily = ManropeFontFamily,
                                fontSize = 16.sp,
                                color = TextDark
                            )
                            Spacer(Modifier.height(8.dp))
                            reviews.take(5).forEach { review ->
                                VehicleReviewItem(review.author, review.rating, review.comment, review.date)
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                    }
                }

                Surface(color = GraySurface, shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                            .navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Total · $days ${if (days == 1) "día" else "días"}",
                                fontSize = 12.sp,
                                fontFamily = ManropeFontFamily,
                                color = TextMuted
                            )
                            Text(
                                "S/ ${v.pricePerDay * days} + S/${v.depositAmount}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = ManropeFontFamily,
                                color = TextDark
                            )
                        }
                        Button(
                            onClick = { onReserve(v.id) },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
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

/** Reseña individual del vehículo: autor, estrellas, comentario y fecha relativa. */
@Composable
private fun VehicleReviewItem(author: String, rating: Int, comment: String, date: String) {
    Surface(
        color = GraySurface,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(author, fontWeight = FontWeight.SemiBold, fontFamily = ManropeFontFamily, fontSize = 14.sp, color = TextDark)
                Spacer(Modifier.weight(1f))
                Text("★".repeat(rating.coerceIn(0, 5)), color = Color(0xFFF59E0B), fontSize = 13.sp)
            }
            if (comment.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(comment, fontSize = 13.sp, color = TextMuted, fontFamily = ManropeFontFamily, lineHeight = 18.sp)
            }
            Spacer(Modifier.height(4.dp))
            Text(date, fontSize = 11.sp, color = TextMuted, fontFamily = ManropeFontFamily)
        }
    }
}

@Composable
private fun Spec(icon: ImageVector, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = BlueAccent, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            fontFamily = ManropeFontFamily,
            color = TextDark
        )
    }
}
