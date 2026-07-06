package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.ui.components.WPBackButton
import com.example.moveo_frontend.ui.components.SectionTitle
import com.example.moveo_frontend.ui.viewmodel.SafetyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyScreen(onBack: () -> Unit) {
    val vm: SafetyViewModel = viewModel()
    val contacts by vm.contacts.collectAsState()
    var onlyWomen by remember { mutableStateOf(true) }
    var shareLive by remember { mutableStateOf(true) }
    var onlyVerified by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }
    val scroll = rememberScrollState()

    if (showAddDialog) {
        AddContactDialog(
            onAdd = { name, phone -> vm.add(name, phone); showAddDialog = false },
            onDismiss = { showAddDialog = false }
        )
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Seguridad") }, navigationIcon = {
            WPBackButton(onClick = onBack)
        })
    }) { padding ->
        Column(Modifier.padding(padding).verticalScroll(scroll).padding(20.dp)) {
            SectionTitle("Preferencias")
            ToggleRow("Solo mujeres", "Solo conductoras verificadas como mujer", onlyWomen) { onlyWomen = it }
            ToggleRow("Compartir viaje en vivo", "Tus contactos verán tu ubicación en tiempo real", shareLive) { shareLive = it }
            ToggleRow("Solo verificados", "Solo permitir conductores con KYC aprobado", onlyVerified) { onlyVerified = it }

            Spacer(Modifier.height(20.dp))
            SectionTitle("Contactos de confianza")
            if (contacts.isEmpty()) {
                Text(
                    "Aún no tienes contactos. Añade a quienes verán tu ubicación en un viaje.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                contacts.forEach { c -> ContactRow(c.name, c.phone) { vm.remove(c) } }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { showAddDialog = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(6.dp))
                Text("Añadir contacto")
            }
        }
    }
}

@Composable
private fun AddContactDialog(onAdd: (String, String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo contacto de confianza") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAdd(name, phone) },
                enabled = name.isNotBlank() && phone.isNotBlank()
            ) { Text("Añadir") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun ToggleRow(title: String, subtitle: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = value, onCheckedChange = onChange)
        }
    }
}

@Composable
private fun ContactRow(name: String, phone: String, onDelete: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(40.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.SemiBold)
                Text(phone, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

