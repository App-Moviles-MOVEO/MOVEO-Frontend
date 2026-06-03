package com.example.moveo_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.moveo_frontend.nav.AppNavGraph
import com.example.moveo_frontend.ui.theme.MoveofrontendTheme
import com.stripe.android.PaymentConfiguration

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializa Stripe con la publishable key (segura para el cliente).
        if (BuildConfig.STRIPE_PUBLISHABLE_KEY.isNotBlank()) {
            PaymentConfiguration.init(applicationContext, BuildConfig.STRIPE_PUBLISHABLE_KEY)
        }
        enableEdgeToEdge()
        setContent {
            MoveofrontendTheme {
                AppNavGraph()
            }
        }
    }
}
