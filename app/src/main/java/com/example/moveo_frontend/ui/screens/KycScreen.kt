package com.example.moveo_frontend.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.moveo_frontend.ui.components.WPBackButton
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.BuildConfig
import com.example.moveo_frontend.ui.theme.BlueAccent
import com.example.moveo_frontend.ui.theme.GrayUI
import com.example.moveo_frontend.ui.theme.ManropeFontFamily
import com.example.moveo_frontend.ui.theme.TextMuted
import com.example.moveo_frontend.ui.viewmodel.KycViewModel
import com.example.moveo_frontend.ui.viewmodel.UiState
import java.io.File

private val KycDark = Color(0xFF1C2532)

private data class KycStepData(val title: String, val subtitle: String)

private val kycSteps = listOf(
    KycStepData("Escanea tu DNI",        "Coloca el frente de tu documento dentro del marco"),
    KycStepData("Reverso del DNI",       "Coloca el reverso de tu documento dentro del marco"),
    KycStepData("Tómate una selfie",     "Mira a la cámara y mantén el rostro centrado"),
    KycStepData("¡Verificación enviada!", "Tu identidad se verificará en pocos minutos")
)

@Composable
fun KycScreen(onFinish: () -> Unit, onBack: () -> Unit = {}) {
    val vm: KycViewModel = viewModel()
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    var step by remember { mutableStateOf(0) }
    val totalSteps = kycSteps.size

    // Documentos elegidos por el usuario (galería). Se suben al backend en el último paso.
    var frontFile by remember { mutableStateOf<File?>(null) }
    var backFile by remember { mutableStateOf<File?>(null) }

    val pickFront = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) { frontFile = copyUriToCache(context, uri, "kyc_front.jpg"); step = 1 }
    }
    val pickBack = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) { backFile = copyUriToCache(context, uri, "kyc_back.jpg"); step = 2 }
    }
    val pickSelfie = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val front = frontFile
        val back = backFile
        if (uri != null && front != null && back != null) {
            val selfie = copyUriToCache(context, uri, "kyc_selfie.jpg")
            vm.submit(front, back, selfie)
            step = 3
        }
    }

    LaunchedEffect(state) {
        if (state is UiState.Success) onFinish()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            WPBackButton(onClick = { if (step > 0) step-- else onBack() })

            Spacer(Modifier.width(12.dp))

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(totalSteps) { i ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .background(
                                color = if (i <= step) BlueAccent else KycDark.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "PASO ${step + 1} DE $totalSteps",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = ManropeFontFamily,
            color = TextMuted,
            letterSpacing = 0.96.sp
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = kycSteps[step].title,
            fontSize = 33.5.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = ManropeFontFamily,
            color = Color.Black,
            letterSpacing = (-0.84).sp
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = kycSteps[step].subtitle,
            fontSize = 17.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = ManropeFontFamily,
            color = TextMuted,
            lineHeight = 21.sp,
            letterSpacing = 0.sp
        )

        Spacer(Modifier.height(24.dp))

        if (step < totalSteps - 1) {
            DniScannerFrame(modifier = Modifier.fillMaxWidth())
        }

        Spacer(Modifier.weight(1f))

        if (state is UiState.Error) {
            Text(
                text = (state as UiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                fontFamily = ManropeFontFamily
            )
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = {
                when (step) {
                    0 -> pickFront.launch("image/*")
                    1 -> pickBack.launch("image/*")
                    2 -> pickSelfie.launch("image/*")
                    else -> onFinish()
                }
            },
            enabled = state !is UiState.Loading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = BlueAccent, contentColor = Color.White)
        ) {
            Text(
                text = when {
                    state is UiState.Loading -> "Enviando..."
                    step < totalSteps - 1 -> "Subir foto"
                    else -> "Finalizar"
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = ManropeFontFamily
            )
        }

        // Atajo SOLO PARA DESARROLLO: salta el escaneo de DNI/selfie para agilizar
        // las pruebas del registro. No aparece en builds release.
        if (BuildConfig.DEBUG && step < totalSteps - 1) {
            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = onFinish,
                enabled = state !is UiState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Omitir escaneo de DNI",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = ManropeFontFamily,
                    color = TextMuted
                )
            }
            Text(
                text = "Solo para desarrollo: omite la verificación para facilitar las pruebas del registro.",
                fontSize = 12.sp,
                fontFamily = ManropeFontFamily,
                color = TextMuted,
                lineHeight = 15.sp,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }

        Spacer(Modifier.height(40.dp))
    }
}

/** Copia la imagen elegida (content Uri) a un archivo temporal para poder subirla como multipart. */
private fun copyUriToCache(context: Context, uri: Uri, name: String): File {
    val file = File(context.cacheDir, name)
    context.contentResolver.openInputStream(uri)?.use { input ->
        file.outputStream().use { output -> input.copyTo(output) }
    }
    return file
}

@Composable
private fun DniScannerFrame(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(85.6f / 54f)
            .background(GrayUI, RoundedCornerShape(18.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val o  = 16.dp.toPx()
            val bs = 31.dp.toPx()
            val r  = 8.dp.toPx()
            val sw = 3.dp.toPx()
            val w  = size.width
            val h  = size.height

            val tlRect = Rect(o,         o,         o + r*2,   o + r*2)
            val trRect = Rect(w-o-r*2,   o,         w-o,       o + r*2)
            val blRect = Rect(o,         h-o-r*2,   o + r*2,   h-o)
            val brRect = Rect(w-o-r*2,   h-o-r*2,   w-o,       h-o)

            drawPath(Path().apply {
                moveTo(o + bs, o)
                lineTo(o + r,  o)
                arcTo(tlRect, -90f, -90f, false)
                lineTo(o, o + bs)
            }, BlueAccent, style = Stroke(sw, cap = StrokeCap.Round))

            drawPath(Path().apply {
                moveTo(w - o - bs, o)
                lineTo(w - o - r,  o)
                arcTo(trRect, -90f, 90f, false)
                lineTo(w - o, o + bs)
            }, BlueAccent, style = Stroke(sw, cap = StrokeCap.Round))

            drawPath(Path().apply {
                moveTo(o, h - o - bs)
                lineTo(o, h - o - r)
                arcTo(blRect, 180f, -90f, false)
                lineTo(o + bs, h - o)
            }, BlueAccent, style = Stroke(sw, cap = StrokeCap.Round))

            drawPath(Path().apply {
                moveTo(w - o, h - o - bs)
                lineTo(w - o, h - o - r)
                arcTo(brRect, 0f, 90f, false)
                lineTo(w - o - bs, h - o)
            }, BlueAccent, style = Stroke(sw, cap = StrokeCap.Round))
        }
    }
}
