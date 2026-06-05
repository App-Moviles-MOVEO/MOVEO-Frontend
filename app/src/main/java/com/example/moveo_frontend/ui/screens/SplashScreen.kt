        package com.example.moveo_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.first

@Composable
fun SplashScreen(onAuthed: () -> Unit, onUnauthed: () -> Unit) {
    LaunchedEffect(Unit) {
        val userId = ServiceLocator.session.userId.first()
        if (!userId.isNullOrBlank()) onAuthed() else onUnauthed()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    )
}
