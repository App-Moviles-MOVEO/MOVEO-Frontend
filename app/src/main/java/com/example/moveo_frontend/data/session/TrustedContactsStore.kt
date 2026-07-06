package com.example.moveo_frontend.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Contacto de confianza del usuario (US10). Solo local: el backend no lo persiste. */
data class TrustedContact(val name: String, val phone: String)

private val Context.trustedContactsDataStore: DataStore<Preferences> by
    preferencesDataStore("trusted_contacts")

/**
 * Almacén local de contactos de confianza (US10). Se serializa como texto plano
 * con separadores de control (nombre/teléfono y contacto/contacto) para no
 * depender de una librería JSON. El backend aún no expone estos contactos.
 */
class TrustedContactsStore(private val context: Context) {
    private object Keys {
        val CONTACTS = stringPreferencesKey("contacts")
    }

    val contacts: Flow<List<TrustedContact>> =
        context.trustedContactsDataStore.data.map { prefs ->
            decode(prefs[Keys.CONTACTS].orEmpty())
        }

    suspend fun add(name: String, phone: String) {
        context.trustedContactsDataStore.edit { prefs ->
            val current = decode(prefs[Keys.CONTACTS].orEmpty())
            prefs[Keys.CONTACTS] = encode(current + TrustedContact(name.trim(), phone.trim()))
        }
    }

    suspend fun remove(contact: TrustedContact) {
        context.trustedContactsDataStore.edit { prefs ->
            val current = decode(prefs[Keys.CONTACTS].orEmpty())
            prefs[Keys.CONTACTS] = encode(current.filterNot { it == contact })
        }
    }

    private companion object {
        // Separadores de control ASCII (Unit / Record Separator), definidos por su
        // código para no depender de caracteres invisibles en el fuente.
        private val FIELD_SEP = 31.toChar()
        private val ITEM_SEP = 30.toChar()

        fun encode(list: List<TrustedContact>): String =
            list.joinToString(ITEM_SEP.toString()) { "${it.name}$FIELD_SEP${it.phone}" }

        fun decode(raw: String): List<TrustedContact> {
            if (raw.isBlank()) return emptyList()
            return raw.split(ITEM_SEP).mapNotNull { item ->
                val parts = item.split(FIELD_SEP)
                if (parts.size == 2 && parts[0].isNotBlank()) TrustedContact(parts[0], parts[1]) else null
            }
        }
    }
}
