package com.example.moveo_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.moveo_frontend.di.ServiceLocator
import com.example.moveo_frontend.nav.AppNavGraph
import com.example.moveo_frontend.nav.Routes
import com.example.moveo_frontend.ui.theme.MoveofrontendTheme
import com.stripe.android.PaymentConfiguration
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.STRIPE_PUBLISHABLE_KEY.isNotBlank()) {
            PaymentConfiguration.init(applicationContext, BuildConfig.STRIPE_PUBLISHABLE_KEY)
        }
        val startDestination = runBlocking {
            val userId = ServiceLocator.session.userId.first()
            val kycDone = ServiceLocator.session.kycCompleted.first()
            when {
                userId.isNullOrBlank() -> Routes.WELCOME
                !kycDone -> Routes.KYC
                else -> Routes.MAIN
            }
        }
        enableEdgeToEdge()
        setContent {
            MoveofrontendTheme {
                AppNavGraph(startDestination = startDestination)
            }
        }
    }
}
