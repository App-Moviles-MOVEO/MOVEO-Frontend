package com.example.moveo_frontend.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.moveo_frontend.data.remote.dto.PaymentMethodDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.paymentMethodsDataStore: DataStore<Preferences> by
    preferencesDataStore("payment_methods")

/**
 * Métodos de pago vinculados por el usuario (US21), persistentes en el dispositivo.
 * El backend no expone /payment-methods; por seguridad solo se guardan datos no
 * sensibles (tipo y una etiqueta con los últimos 4 dígitos o el número Yape/Plin).
 */
class PaymentMethodsStore(private val context: Context) {
    private object Keys {
        val METHODS = stringPreferencesKey("methods")
    }

    val methods: Flow<List<PaymentMethodDto>> =
        context.paymentMethodsDataStore.data.map { prefs ->
            decode(prefs[Keys.METHODS].orEmpty())
        }

    suspend fun add(type: String, display: String) {
        context.paymentMethodsDataStore.edit { prefs ->
            val current = decode(prefs[Keys.METHODS].orEmpty())
            val item = PaymentMethodDto("pm_${System.currentTimeMillis()}", type, display)
            prefs[Keys.METHODS] = encode(current + item)
        }
    }

    suspend fun remove(id: String) {
        context.paymentMethodsDataStore.edit { prefs ->
            val current = decode(prefs[Keys.METHODS].orEmpty())
            prefs[Keys.METHODS] = encode(current.filterNot { it.id == id })
        }
    }

    private companion object {
        // Separadores de control ASCII (Unit / Record Separator), definidos por su
        // código para no depender de caracteres invisibles en el fuente.
        private val FIELD_SEP = 31.toChar()
        private val ITEM_SEP = 30.toChar()

        fun encode(list: List<PaymentMethodDto>): String =
            list.joinToString(ITEM_SEP.toString()) { "${it.id}$FIELD_SEP${it.type}$FIELD_SEP${it.display}" }

        fun decode(raw: String): List<PaymentMethodDto> {
            if (raw.isBlank()) return emptyList()
            return raw.split(ITEM_SEP).mapNotNull { item ->
                val p = item.split(FIELD_SEP)
                if (p.size == 3 && p[0].isNotBlank()) PaymentMethodDto(p[0], p[1], p[2]) else null
            }
        }
    }
}
