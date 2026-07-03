package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.ui.components.WPBackButton
import com.example.moveo_frontend.ui.components.WPButton
import com.example.moveo_frontend.ui.viewmodel.AuthViewModel
import com.example.moveo_frontend.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    val vm: AuthViewModel = viewModel()
    val forgotState by vm.forgot.collectAsState()
    val resetState by vm.reset.collectAsState()

    var email by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    // Cuando el backend (en dev) devuelve el resetToken, lo pre-rellenamos para facilitar la prueba.
    LaunchedEffect(forgotState) {
        val t = (forgotState as? UiState.Success)?.data
        if (!t.isNullOrBlank() && token.isBlank()) token = t
    }

    // El paso 2 se muestra una vez que el "olvidé mi contraseña" fue exitoso.
    val requested = forgotState is UiState.Success
    val done = resetState is UiState.Success

    Scaffold(topBar = {
        TopAppBar(title = { Text("Recuperar contraseña") }, navigationIcon = {
            WPBackButton(onClick = onBack)
        })
    }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            if (done) {
                Text("¡Listo!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Tu contraseña se restableció. Ya puedes iniciar sesión con la nueva.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(24.dp))
                WPButton(text = "Volver a iniciar sesión", onClick = onBack)
                return@Column
            }

            Text("Olvidaste tu contraseña", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                "Ingresa tu correo y te enviaremos un código para restablecerla.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                enabled = !requested,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            if (!requested) {
                (forgotState as? UiState.Error)?.let {
                    Text(it.message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                }
                WPButton(
                    text = if (forgotState is UiState.Loading) "Enviando..." else "Enviar código",
                    enabled = forgotState !is UiState.Loading,
                    onClick = { vm.doForgot(email) }
                )
            } else {
                Text(
                    "Revisa tu correo e ingresa el código recibido.",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("Código de restablecimiento") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nueva contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                (resetState as? UiState.Error)?.let {
                    Text(it.message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                }
                WPButton(
                    text = if (resetState is UiState.Loading) "Restableciendo..." else "Restablecer contraseña",
                    enabled = resetState !is UiState.Loading,
                    onClick = { vm.doReset(token, newPassword) }
                )
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { vm.doForgot(email) }) { Text("Reenviar código") }
            }
        }
    }
}
