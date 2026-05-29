package com.example.moveo_frontend.data.session

import android.content.Context
import androidx.datastore.preferences.core.Preferences
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
    }

    val token: Flow<String?> = context.sessionDataStore.data.map { it[Keys.TOKEN] }
    val userId: Flow<String?> = context.sessionDataStore.data.map { it[Keys.USER_ID] }
    val userName: Flow<String?> = context.sessionDataStore.data.map { it[Keys.USER_NAME] }
    val userEmail: Flow<String?> = context.sessionDataStore.data.map { it[Keys.USER_EMAIL] }
    val userRole: Flow<String?> = context.sessionDataStore.data.map { it[Keys.USER_ROLE] }

    suspend fun tokenBlocking(): String? = context.sessionDataStore.data.first()[Keys.TOKEN]

    suspend fun save(token: String, userId: String, name: String, email: String, role: String) {
        context.sessionDataStore.edit {
            it[Keys.TOKEN] = token
            it[Keys.USER_ID] = userId
            it[Keys.USER_NAME] = name
            it[Keys.USER_EMAIL] = email
            it[Keys.USER_ROLE] = role
        }
    }

    suspend fun clear() {
        context.sessionDataStore.edit { it.clear() }
    }
}
