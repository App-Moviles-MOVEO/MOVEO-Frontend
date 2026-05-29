package com.example.moveo_frontend.di

import android.content.Context
import com.example.moveo_frontend.data.remote.ApiClient
import com.example.moveo_frontend.data.repository.AuthRepository
import com.example.moveo_frontend.data.repository.BillingRepository
import com.example.moveo_frontend.data.repository.CarpoolingRepository
import com.example.moveo_frontend.data.repository.OperationsRepository
import com.example.moveo_frontend.data.repository.RentalRepository
import com.example.moveo_frontend.data.session.SessionManager

object ServiceLocator {
    lateinit var session: SessionManager
        private set
    lateinit var authRepo: AuthRepository
        private set
    lateinit var rentalRepo: RentalRepository
        private set
    lateinit var carpoolingRepo: CarpoolingRepository
        private set
    lateinit var billingRepo: BillingRepository
        private set
    lateinit var operationsRepo: OperationsRepository
        private set

    fun init(context: Context) {
        session = SessionManager(context.applicationContext)
        authRepo = AuthRepository(ApiClient.authApi(session), session)
        rentalRepo = RentalRepository(ApiClient.rentalApi(session))
        carpoolingRepo = CarpoolingRepository(ApiClient.carpoolingApi(session))
        billingRepo = BillingRepository(ApiClient.billingApi(session))
        operationsRepo = OperationsRepository(ApiClient.operationsApi(session))
    }
}
