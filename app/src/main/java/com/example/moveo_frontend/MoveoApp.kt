package com.example.moveo_frontend

import android.app.Application
import com.example.moveo_frontend.di.ServiceLocator

class MoveoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}
