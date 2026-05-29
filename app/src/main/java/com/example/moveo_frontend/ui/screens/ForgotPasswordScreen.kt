package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.ui.components.WPButton
import com.example.moveo_frontend.ui.viewmodel.AuthViewModel
import com.example.moveo_frontend.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    val vm: AuthViewModel = viewModel()
    val state by vm.forgot.collectAsState()
    var email by remember { mutableStateOf("") }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Recuperar contraseña") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
        })
    }) { padding ->
        Column(Modifier.padding(padding).padding(24.dp)) {
            Text("Olvidaste tu contraseña", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                "Te enviaremos un enlace para restablecerla.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))

            when (val s = state) {
                is UiState.Error -> Text(s.message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                is UiState.Success -> Text("Correo enviado. Revisa tu bandeja.", color = MaterialTheme.colorScheme.tertiary, fontSize = 13.sp)
                else -> Unit
            }

            Spacer(Modifier.height(24.dp))
            WPButton(
                text = if (state is UiState.Loading) "Enviando..." else "Enviar enlace",
                enabled = state !is UiState.Loading,
                onClick = { vm.doForgot(email) }
            )
        }
    }
}
