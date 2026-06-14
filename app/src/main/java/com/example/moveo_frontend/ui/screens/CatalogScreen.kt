package com.example.moveo_frontend.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.moveo_frontend.data.Vehicle
import com.example.moveo_frontend.ui.components.WPBackButton
import com.example.moveo_frontend.ui.theme.BlueAccent
import com.example.moveo_frontend.ui.theme.GrayBackground
import com.example.moveo_frontend.ui.theme.GraySurface
import com.example.moveo_frontend.ui.theme.ManropeFontFamily
import com.example.moveo_frontend.ui.theme.TextDark
import com.example.moveo_frontend.ui.theme.TextMuted
import com.example.moveo_frontend.ui.components.StateContainer
import com.example.moveo_frontend.ui.viewmodel.UiState
import com.example.moveo_frontend.ui.viewmodel.VehiclesViewModel
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val DAY_MS = 24L * 60 * 60 * 1000

/** "9 jun" desde millis UTC (formatea en UTC para no correrse un día). */
private fun dayLabel(millis: Long): String =
    SimpleDateFormat("d MMM", Locale.forLanguageTag("es"))
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date(millis))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(onBack: () -> Unit, onVehicleClick: (String) -> Unit, onPublish: (() -> Unit)? = null) {
    val vm: VehiclesViewModel = viewModel()
    val state by vm.state.collectAsState()
    val filter by vm.filter.collectAsState()
    val district by vm.district.collectAsState()
    val start by vm.startMillis.collectAsState()
    val end by vm.endMillis.collectAsState()
    val userLocation by vm.userLocation.collectAsState()

    var showDates by remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun fetchLocation() {
        try {
            LocationServices.getFusedLocationProviderClient(context).lastLocation
                .addOnSuccessListener { loc ->
                    // En emulador sin fix GPS lastLocation puede ser null: usamos el centro de Lima.
                    if (loc != null) vm.setUserLocation(loc.latitude, loc.longitude)
                    else vm.setUserLocation(-12.0464, -77.0428)
                }
        } catch (_: SecurityException) {
            // El permiso fue revocado entre el chequeo y la llamada; se ignora.
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) fetchLocation() }

    Scaffold(
        containerColor = GrayBackground,
        floatingActionButton = {
            if (onPublish != null) {
                ExtendedFloatingActionButton(
                    onClick = onPublish,
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Publicar auto") }
                )
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            // ---- Header ----
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WPBackButton(onClick = onBack)
                Text(
                    "Alquilar auto",
                    modifier = Modifier.weight(1f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = ManropeFontFamily,
                    color = TextDark,
                    letterSpacing = (-0.4).sp
                )
            }

            // ---- Búsqueda por distrito + fechas ----
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f).height(46.dp)
                ) {
                    Row(
                        Modifier.padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(10.dp))
                        BasicTextField(
                            value = district,
                            onValueChange = { vm.setDistrict(it) },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 13.sp,
                                fontFamily = ManropeFontFamily,
                                color = TextDark
                            ),
                            modifier = Modifier.weight(1f),
                            decorationBox = { inner ->
                                if (district.isEmpty()) {
                                    Text("Distrito · Lima", fontSize = 13.sp, color = TextMuted, fontFamily = ManropeFontFamily)
                                }
                                inner()
                            }
                        )
                    }
                }
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(46.dp).clickable { showDates = true }
                ) {
                    Row(
                        Modifier.padding(horizontal = 14.dp).fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, null, tint = if (start != null) BlueAccent else TextDark, modifier = Modifier.size(15.dp))
                        Text(
                            if (start != null && end != null) "${dayLabel(start!!)}–${dayLabel(end!!)}" else "Fechas",
                            fontSize = 13.sp,
                            fontFamily = ManropeFontFamily,
                            fontWeight = if (start != null) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (start != null) BlueAccent else TextDark
                        )
                    }
                }
            }

            // ---- Chips ----
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(vm.filters) { f ->
                    FilterChip(
                        selected = f == filter,
                        onClick = { vm.setFilter(f) },
                        label = { Text(f) }
                    )
                }
            }

            // ---- Contador + orden por cercanía ----
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val count = (state as? UiState.Success)?.data?.size
                Text(
                    when {
                        count == null -> "Buscando autos…"
                        count == 1 -> "1 auto disponible"
                        else -> "$count autos disponibles"
                    },
                    fontSize = 13.sp,
                    color = TextMuted,
                    fontFamily = ManropeFontFamily,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = {
                    if (userLocation == null) {
                        val granted = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                        if (granted) fetchLocation()
                        else permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                    }
                }) {
                    Icon(
                        Icons.Default.NearMe, null,
                        tint = if (userLocation != null) BlueAccent else TextDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        if (userLocation != null) "Más cercano ✓" else "Más cercano",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = ManropeFontFamily,
                        color = if (userLocation != null) BlueAccent else TextDark
                    )
                }
            }

            // ---- Lista ----
            StateContainer(state, onRetry = { vm.load() }) { vehicles ->
                if (vehicles.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            if (start != null) "No hay autos libres en esas fechas" else "No hay vehículos disponibles",
                            color = TextMuted,
                            fontFamily = ManropeFontFamily
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(vehicles) { v ->
                            RentCarCard(v, vm.distanceKm(v)) { onVehicleClick(v.id) }
                        }
                    }
                }
            }
        }
    }

    if (showDates) {
        val rangeState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = start,
            initialSelectedEndDateMillis = end,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    utcTimeMillis >= System.currentTimeMillis() - DAY_MS
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDates = false },
            confirmButton = {
                TextButton(
                    enabled = rangeState.selectedStartDateMillis != null && rangeState.selectedEndDateMillis != null,
                    onClick = {
                        vm.setDates(rangeState.selectedStartDateMillis!!, rangeState.selectedEndDateMillis!!)
                        showDates = false
                    }
                ) { Text("Aplicar") }
            },
            dismissButton = {
                TextButton(onClick = { vm.clearDates(); showDates = false }) { Text("Quitar fechas") }
            }
        ) {
            DateRangePicker(
                state = rangeState,
                showModeToggle = false,
                modifier = Modifier.heightIn(max = 440.dp),
                title = {
                    Text(
                        "¿Cuándo necesitas el auto?",
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    }
}

@Composable
private fun RentCarCard(v: Vehicle, distanceKm: Double?, onClick: () -> Unit) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (v.imageUrl != null) {
                AsyncImage(
                    model = v.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp))
                )
            } else {
                Box(
                    Modifier.size(100.dp).background(GraySurface, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.DirectionsCar, null, tint = TextMuted, modifier = Modifier.size(44.dp))
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "${v.brand} ${v.model}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = ManropeFontFamily,
                            color = TextDark,
                            letterSpacing = (-0.3).sp
                        )
                        if (v.ownerVerified) {
                            Icon(Icons.Default.CheckCircle, null, tint = BlueAccent, modifier = Modifier.size(14.dp))
                        }
                    }
                    Text(
                        "${v.type} · ${v.transmission.lowercase()} · ${v.year}",
                        fontSize = 11.sp, color = TextMuted, fontFamily = ManropeFontFamily,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(11.dp))
                        Text("${v.rating}", fontSize = 11.sp, color = TextMuted, fontFamily = ManropeFontFamily)
                        Text("·", fontSize = 11.sp, color = TextMuted)
                        Text(
                            distanceKm?.let { String.format(Locale.US, "%.1f km", it) } ?: v.place,
                            fontSize = 11.sp, color = TextMuted, fontFamily = ManropeFontFamily
                        )
                        if (v.ownerName.isNotBlank()) {
                            Text("·", fontSize = 11.sp, color = TextMuted)
                            Text(v.ownerName, fontSize = 11.sp, color = TextMuted, fontFamily = ManropeFontFamily, maxLines = 1)
                        }
                    }
                }
                Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 6.dp)) {
                    Text(
                        "S/${v.pricePerDay}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = ManropeFontFamily,
                        color = TextDark,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        "/día",
                        fontSize = 11.sp, color = TextMuted, fontFamily = ManropeFontFamily,
                        modifier = Modifier.padding(bottom = 1.dp, start = 2.dp)
                    )
                }
            }
        }
    }
}