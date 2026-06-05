package com.example.moveo_frontend.data.session

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore("session")

class SessionManager(private val context: Context) {
    private object Keys {
        val TOKEN = stringPreferencesKey("auth_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_ROLE = stringPreferencesKey("user_role")
        val KYC_COMPLETED = booleanPreferencesKey("kyc_completed")
    }

    val token: Flow<String?> = context.sessionDataStore.data.map { it[Keys.TOKEN] }
    val userId: Flow<String?> = context.sessionDataStore.data.map { it[Keys.USER_ID] }
    val userName: Flow<String?> = context.sessionDataStore.data.map { it[Keys.USER_NAME] }
    val userEmail: Flow<String?> = context.sessionDataStore.data.map { it[Keys.USER_EMAIL] }
    val userRole: Flow<String?> = context.sessionDataStore.data.map { it[Keys.USER_ROLE] }
    val kycCompleted: Flow<Boolean> = context.sessionDataStore.data.map { it[Keys.KYC_COMPLETED] ?: false }

    suspend fun tokenBlocking(): String? = context.sessionDataStore.data.first()[Keys.TOKEN]
    suspend fun userIdBlocking(): String? = context.sessionDataStore.data.first()[Keys.USER_ID]
    suspend fun kycCompletedBlocking(): Boolean = context.sessionDataStore.data.first()[Keys.KYC_COMPLETED] ?: false

    suspend fun setKycCompleted() {
        context.sessionDataStore.edit { it[Keys.KYC_COMPLETED] = true }
    }

    suspend fun save(userId: String, name: String, email: String, role: String, token: String = "", kycCompleted: Boolean = true) {
        context.sessionDataStore.edit {
            it[Keys.TOKEN] = token
            it[Keys.USER_ID] = userId
            it[Keys.USER_NAME] = name
            it[Keys.USER_EMAIL] = email
            it[Keys.USER_ROLE] = role
            it[Keys.KYC_COMPLETED] = kycCompleted
        }
    }

    suspend fun clear() {
        context.sessionDataStore.edit { it.clear() }
    }
}
