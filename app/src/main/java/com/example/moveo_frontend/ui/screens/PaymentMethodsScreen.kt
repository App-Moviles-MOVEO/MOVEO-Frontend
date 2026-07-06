package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moveo_frontend.ui.components.WPBackButton
import com.example.moveo_frontend.ui.components.StateContainer
import com.example.moveo_frontend.ui.viewmodel.PaymentMethodsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(onBack: () -> Unit) {
    val vm: PaymentMethodsViewModel = viewModel()
    val state by vm.methods.collectAsState()
    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Métodos de pago") }, navigationIcon = {
                WPBackButton(onClick = onBack)
            })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAdd = true },
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Vincular método") }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            StateContainer(state, onRetry = { vm.load() }) { methods ->
                if (methods.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Aún no tienes métodos de pago. Agrega una tarjeta para empezar.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(methods, key = { it.id }) { m ->
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(14.dp),
                                tonalElevation = 1.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CreditCard, null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(m.display, fontWeight = FontWeight.SemiBold)
                                        Text(m.type, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    IconButton(onClick = { vm.remove(m.id) }) {
                                        Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAdd) {
        var tab by remember { mutableStateOf(0) } // 0 = tarjeta, 1 = Yape/Plin
        var last4 by remember { mutableStateOf("") }
        var provider by remember { mutableStateOf("Yape") }
        var phone by remember { mutableStateOf("") }
        val cardValid = last4.length == 4
        val walletValid = phone.length in 9..12
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("Vincular método de pago") },
            text = {
                Column {
                    TabRow(selectedTabIndex = tab) {
                        Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Tarjeta") })
                        Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Yape/Plin") })
                    }
                    Spacer(Modifier.height(12.dp))
                    if (tab == 0) {
                        Text("Ingresa los últimos 4 dígitos (demo).", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = last4,
                            onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) last4 = it },
                            label = { Text("Últimos 4 dígitos") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(selected = provider == "Yape", onClick = { provider = "Yape" }, label = { Text("Yape") })
                            FilterChip(selected = provider == "Plin", onClick = { provider = "Plin" }, label = { Text("Plin") })
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { if (it.length <= 12 && it.all(Char::isDigit)) phone = it },
                            label = { Text("Número de celular") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = if (tab == 0) cardValid else walletValid,
                    onClick = {
                        if (tab == 0) vm.addCard(last4) else vm.addWallet(provider, phone)
                        showAdd = false
                    }
                ) { Text("Vincular") }
            },
            dismissButton = { TextButton(onClick = { showAdd = false }) { Text("Cancelar") } }
        )
    }
}