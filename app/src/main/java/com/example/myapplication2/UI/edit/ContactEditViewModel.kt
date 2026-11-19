package com.example.myapplication2.UI.edit

import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication2.data.Contact
import com.example.myapplication2.data.ContactRepository
import com.example.myapplication2.UI.navigation.ContactsDestinations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ContactFormState(
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val isFavorite: Boolean = false,
    val showErrors: Boolean = false,
    val isSaved: Boolean = false,
) {
    val nameError: Boolean get() = showErrors && name.isBlank()
    val phoneError: Boolean get() = showErrors && !isValidPhone(phone)
    val emailError: Boolean get() = showErrors && email.isNotBlank() && !isValidEmail(email)
    val isValid: Boolean
        get() = name.isNotBlank() && isValidPhone(phone) && (email.isBlank() || isValidEmail(email))
}

private fun isValidPhone(phone: String) = phone.count(Char::isDigit) >= 3

private fun isValidEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

class ContactEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ContactRepository,
) : ViewModel() {

    private val contactId: Long =
        savedStateHandle[ContactsDestinations.CONTACT_ID_ARG] ?: ContactsDestinations.NEW_CONTACT_ID

    val isNewContact: Boolean = contactId == ContactsDestinations.NEW_CONTACT_ID

    private val _state = MutableStateFlow(ContactFormState())
    val state: StateFlow<ContactFormState> = _state.asStateFlow()

    init {
        if (!isNewContact) {
            viewModelScope.launch {
                repository.getContact(contactId)?.let { contact ->
                    _state.update {
                        it.copy(
                            name = contact.name,
                            phone = contact.phone,
                            email = contact.email,
                            isFavorite = contact.isFavorite,
                        )
                    }
                }
            }
        }
    }

    fun onNameChange(value: String) = _state.update { it.copy(name = value) }

    fun onPhoneChange(value: String) = _state.update { it.copy(phone = value) }

    fun onEmailChange(value: String) = _state.update { it.copy(email = value) }

    fun onFavoriteChange(value: Boolean) = _state.update { it.copy(isFavorite = value) }

    fun save() {
        val form = _state.value
        if (!form.isValid) {
            _state.update { it.copy(showErrors = true) }
            return
        }
        viewModelScope.launch {
            repository.save(
                Contact(
                    id = if (isNewContact) 0 else contactId,
                    name = form.name.trim(),
                    phone = form.phone.trim(),
                    email = form.email.trim(),
                    isFavorite = form.isFavorite,
                )
            )
            _state.update { it.copy(isSaved = true) }
        }
    }
}
