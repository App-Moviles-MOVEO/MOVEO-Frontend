package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.data.UserRole
import com.example.moveo_frontend.ui.components.WPBackButton
import com.example.moveo_frontend.ui.components.WPButton
import com.example.moveo_frontend.ui.viewmodel.AuthViewModel
import com.example.moveo_frontend.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onBack: () -> Unit, onContinue: () -> Unit) {
    val vm: AuthViewModel = viewModel()
    val state by vm.register.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") } // "female" | "male" | "" (opcional)
    val scroll = rememberScrollState()

    LaunchedEffect(state) {
        if (state is UiState.Success) onContinue()
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Crear cuenta") }, navigationIcon = {
            WPBackButton(onClick = onBack)
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

            // US11: obligatorio; habilita/segmenta las rutas de carpool exclusivas para mujeres.
            Text("Género", fontWeight = FontWeight.SemiBold)
            Text(
                "Se usa para las rutas de carpool exclusivas para mujeres.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = gender == "female",
                    onClick = { gender = if (gender == "female") "" else "female" },
                    label = { Text("Femenino") }
                )
                FilterChip(
                    selected = gender == "male",
                    onClick = { gender = if (gender == "male") "" else "male" },
                    label = { Text("Masculino") }
                )
            }
            Spacer(Modifier.height(20.dp))

            if (state is UiState.Error) {
                Text(
                    (state as UiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(8.dp))
            }

            WPButton(
                text = if (state is UiState.Loading) "Creando..." else "Continuar a verificación",
                onClick = { vm.doRegister(name, email, phone, password, UserRole.RENTER.name, gender) },
                enabled = gender.isNotBlank() && state !is UiState.Loading
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}
