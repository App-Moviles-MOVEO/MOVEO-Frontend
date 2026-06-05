package com.example.moveo_frontend.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moveo_frontend.ui.components.WPBackButton
import com.example.moveo_frontend.ui.components.SectionTitle

private val faqs = listOf(
    "¿Cómo reservo un auto?" to "Entra al catálogo, elige un vehículo, toca \"Reservar\", selecciona tus fechas y completa el pago seguro con Stripe.",
    "¿Qué es la garantía (Escrow)?" to "Es un monto retenido de forma segura durante el alquiler. Si devuelves el auto sin daños y a tiempo, se te reembolsa completo.",
    "¿Cómo publico mi auto?" to "Ve a la pestaña Catálogo y toca \"Publicar auto\". Completa los datos del vehículo y precio por día.",
    "¿Cómo funciona el carpooling?" to "Busca rutas en la pestaña Carpool, reserva un asiento o publica tu propio viaje para compartir gastos.",
    "¿Es seguro pagar en la app?" to "Sí. Los pagos los procesa Stripe; MOVEO nunca almacena los datos de tu tarjeta."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(onBack: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Ayuda y soporte") }, navigationIcon = {
            WPBackButton(onClick = onBack)
        })
    }) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
        ) {
            SectionTitle("Preguntas frecuentes")
            faqs.forEach { (q, a) -> FaqItem(q, a) }

            Spacer(Modifier.height(24.dp))
            SectionTitle("¿Necesitas más ayuda?")
            ContactRow(Icons.Default.Email, "soporte@moveo.pe", "Escríbenos un correo")
            ContactRow(Icons.Default.Phone, "+51 999 888 777", "Lun a Vie, 9am - 6pm")
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
    ) {
        Column(Modifier.clickable { expanded = !expanded }.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(question, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null)
            }
            AnimatedVisibility(expanded) {
                Text(answer, fontSize = 13.sp, lineHeight = 19.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
private fun ContactRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(14.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}