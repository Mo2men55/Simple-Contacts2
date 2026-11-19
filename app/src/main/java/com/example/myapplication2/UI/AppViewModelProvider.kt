package com.example.myapplication2.UI

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication2.ContactsApplication
import com.example.myapplication2.UI.detail.ContactDetailViewModel
import com.example.myapplication2.UI.edit.ContactEditViewModel
import com.example.myapplication2.UI.list.ContactListViewModel

/** Simple manual dependency injection: builds every ViewModel with the shared repository. */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { ContactListViewModel(contactsApplication().repository) }
        initializer {
            ContactDetailViewModel(createSavedStateHandle(), contactsApplication().repository)
        }
        initializer {
            ContactEditViewModel(createSavedStateHandle(), contactsApplication().repository)
        }
    }
}

private fun CreationExtras.contactsApplication(): ContactsApplication =
    this[APPLICATION_KEY] as ContactsApplication
