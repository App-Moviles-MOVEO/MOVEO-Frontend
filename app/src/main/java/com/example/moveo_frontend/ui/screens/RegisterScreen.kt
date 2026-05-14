package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moveo_frontend.data.UserRole
import com.example.moveo_frontend.ui.components.WPButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onBack: () -> Unit, onContinue: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf<UserRole?>(null) }
    val scroll = rememberScrollState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Crear cuenta") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
        })
    }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(scroll)
        ) {
            Text("Únete a WheelsPe", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre completo") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo electrónico") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(20.dp))

            Text("Elige tu rol", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(10.dp))
            RoleOption("Proveedor", "Publico mis vehículos", role == UserRole.PROVIDER) { role = UserRole.PROVIDER }
            Spacer(Modifier.height(10.dp))
            RoleOption("Arrendatario", "Alquilo vehículos", role == UserRole.RENTER) { role = UserRole.RENTER }
            Spacer(Modifier.height(10.dp))
            RoleOption("Pasajero", "Busco viajes compartidos", role == UserRole.PASSENGER) { role = UserRole.PASSENGER }
            Spacer(Modifier.height(28.dp))

            WPButton("Continuar a verificación", onClick = onContinue, enabled = role != null)
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RoleOption(title: String, subtitle: String, selected: Boolean, onClick: () -> Unit) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val bg = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(14.dp))
            .border(if (selected) 2.dp else 1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(Modifier.width(8.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
