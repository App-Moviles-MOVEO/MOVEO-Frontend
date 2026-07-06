package com.example.moveo_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moveo_frontend.data.session.TrustedContact
import com.example.moveo_frontend.di.ServiceLocator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** US10: contactos de confianza persistentes (almacenamiento local). */
class SafetyViewModel : ViewModel() {
    private val store = ServiceLocator.trustedContacts

    val contacts = store.contacts.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    fun add(name: String, phone: String) {
        if (name.isBlank() || phone.isBlank()) return
        viewModelScope.launch { store.add(name, phone) }
    }

    fun remove(contact: TrustedContact) {
        viewModelScope.launch { store.remove(contact) }
    }
}
