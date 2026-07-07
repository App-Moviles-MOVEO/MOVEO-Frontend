package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.ui.components.WPBackButton
import com.example.moveo_frontend.ui.components.SectionTitle
import com.example.moveo_frontend.ui.viewmodel.SettingsViewModel
import com.example.moveo_frontend.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onTerms: () -> Unit,
    onLoggedOut: () -> Unit
) {
    val vm: SettingsViewModel = viewModel()
    var pushNotifs by remember { mutableStateOf(true) }
    var tripReminders by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }
    var showChangePassword by remember { mutableStateOf(false) }
    var showDeleteAccount by remember { mutableStateOf(false) }
    var showPartnership by remember { mutableStateOf(false) }

    val deleteState by vm.deleteAccount.collectAsState()
    LaunchedEffect(deleteState) {
        if (deleteState is UiState.Success) { vm.resetDeleteAccount(); onLoggedOut() }
    }

    if (showChangePassword) ChangePasswordDialog(vm) { showChangePassword = false }
    if (showDeleteAccount) DeleteAccountDialog(vm, deleteState) { showDeleteAccount = false }
    if (showPartnership) PartnershipDialog(vm) { showPartnership = false }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Configuración") }, navigationIcon = {
            WPBackButton(onClick = onBack)
        })
    }) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
        ) {
            SectionTitle("Notificaciones")
            SwitchRow(Icons.Default.Notifications, "Notificaciones push", "Reservas, mensajes y alertas", pushNotifs) { pushNotifs = it }
            SwitchRow(Icons.Default.Notifications, "Recordatorios de viaje", "Avisos antes de cada viaje", tripReminders) { tripReminders = it }

            SectionTitle("Apariencia")
            SwitchRow(Icons.Default.DarkMode, "Modo oscuro", "Usar tema oscuro", darkMode) { darkMode = it }

            SectionTitle("Cuenta")
            NavRow(Icons.Default.Lock, "Cambiar contraseña", "Actualiza tu contraseña") { showChangePassword = true }
            NavRow(Icons.Default.Language, "Idioma", "Español") {}
            NavRow(Icons.Default.Business, "Alianza corporativa", "¿Tu empresa quiere unirse?") { showPartnership = true }
            NavRow(Icons.AutoMirrored.Filled.HelpOutline, "Ayuda y soporte", "Preguntas frecuentes", onClick = onHelp)

            SectionTitle("Legal")
            NavRow(Icons.Default.Description, "Términos y condiciones", "Lee los términos de uso", onClick = onTerms)

            SectionTitle("Zona de peligro")
            NavRow(
                Icons.Default.DeleteForever, "Eliminar cuenta",
                "Borra tu cuenta y tus datos de forma permanente",
                tint = MaterialTheme.colorScheme.error
            ) { showDeleteAccount = true }

            Spacer(Modifier.height(20.dp))
            Text("MOVEO · versión 1.0", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ChangePasswordDialog(vm: SettingsViewModel, onDismiss: () -> Unit) {
    val state by vm.changePassword.collectAsState()
    var current by remember { mutableStateOf("") }
    var new by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is UiState.Success) { vm.resetChangePassword(); onDismiss() }
    }

    AlertDialog(
        onDismissRequest = { vm.resetChangePassword(); onDismiss() },
        title = { Text("Cambiar contraseña") },
        text = {
            Column {
                PasswordField("Contraseña actual", current) { current = it }
                Spacer(Modifier.height(10.dp))
                PasswordField("Nueva contraseña", new) { new = it }
                Spacer(Modifier.height(10.dp))
                PasswordField("Confirmar nueva contraseña", confirm) { confirm = it }
                if (state is UiState.Error) {
                    Spacer(Modifier.height(8.dp))
                    Text((state as UiState.Error).message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = state !is UiState.Loading,
                onClick = { vm.changePassword(current, new, confirm) }
            ) { Text(if (state is UiState.Loading) "Guardando..." else "Guardar") }
        },
        dismissButton = { TextButton(onClick = { vm.resetChangePassword(); onDismiss() }) { Text("Cancelar") } }
    )
}

@Composable
private fun DeleteAccountDialog(vm: SettingsViewModel, state: UiState<Unit>, onDismiss: () -> Unit) {
    var confirmText by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { if (state !is UiState.Loading) onDismiss() },
        title = { Text("Eliminar cuenta") },
        text = {
            Column {
                Text(
                    "Esta acción es permanente e inmediata: se borrarán tu cuenta y tus datos. " +
                        "Escribe ELIMINAR para confirmar.",
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = confirmText,
                    onValueChange = { confirmText = it },
                    label = { Text("Escribe ELIMINAR") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (state is UiState.Error) {
                    Spacer(Modifier.height(8.dp))
                    Text(state.message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = confirmText.trim().equals("ELIMINAR", ignoreCase = true) && state !is UiState.Loading,
                onClick = { vm.deleteAccount() }
            ) {
                Text(
                    if (state is UiState.Loading) "Eliminando..." else "Eliminar",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun PartnershipDialog(vm: SettingsViewModel, onDismiss: () -> Unit) {
    val state by vm.partnership.collectAsState()
    var company by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    if (state is UiState.Success) {
        AlertDialog(
            onDismissRequest = { vm.resetPartnership(); onDismiss() },
            title = { Text("Solicitud enviada") },
            text = { Text("Recibimos tu interés. El equipo comercial te contactará al correo indicado.") },
            confirmButton = { TextButton(onClick = { vm.resetPartnership(); onDismiss() }) { Text("Listo") } }
        )
        return
    }

    AlertDialog(
        onDismissRequest = { vm.resetPartnership(); onDismiss() },
        title = { Text("Alianza corporativa") },
        text = {
            Column {
                Text("Cuéntanos de tu empresa y te contactaremos.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(company, { company = it }, label = { Text("Empresa") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    email, { email = it }, label = { Text("Correo de contacto") }, singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(message, { message = it }, label = { Text("Mensaje (opcional)") }, modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp))
                if (state is UiState.Error) {
                    Spacer(Modifier.height(8.dp))
                    Text((state as UiState.Error).message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = state !is UiState.Loading,
                onClick = { vm.requestPartnership(company, email, message) }
            ) { Text(if (state is UiState.Loading) "Enviando..." else "Enviar") }
        },
        dismissButton = { TextButton(onClick = { vm.resetPartnership(); onDismiss() }) { Text("Cancelar") } }
    )
}

@Composable
private fun PasswordField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SwitchRow(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun NavRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = tint)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
