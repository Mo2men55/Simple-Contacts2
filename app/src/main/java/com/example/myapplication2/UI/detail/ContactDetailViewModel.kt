package com.example.myapplication2.UI.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication2.data.Contact
import com.example.myapplication2.data.ContactRepository
import com.example.myapplication2.UI.navigation.ContactsDestinations
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ContactDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ContactRepository,
) : ViewModel() {

    private val contactId: Long = checkNotNull(savedStateHandle[ContactsDestinations.CONTACT_ID_ARG])

    val contact: StateFlow<Contact?> = repository.observeContact(contactId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initialValue = null)

    fun toggleFavorite() {
        contact.value?.let { viewModelScope.launch { repository.toggleFavorite(it) } }
    }

    fun delete(onDeleted: () -> Unit) {
        val current = contact.value ?: return
        viewModelScope.launch {
            repository.delete(current)
            onDeleted()
        }
    }
}
