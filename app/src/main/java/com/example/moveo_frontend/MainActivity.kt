package com.example.moveo_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.moveo_frontend.nav.AppNavGraph
import com.example.moveo_frontend.ui.theme.MoveofrontendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveofrontendTheme {
                AppNavGraph()
            }
        }
    }
}
