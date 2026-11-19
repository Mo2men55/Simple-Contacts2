package com.example.myapplication2.data

import kotlinx.coroutines.flow.Flow

class ContactRepository(private val dao: ContactDao) {

    fun searchContacts(query: String): Flow<List<Contact>> = dao.search(query.trim())

    fun observeContact(id: Long): Flow<Contact?> = dao.observeById(id)

    suspend fun getContact(id: Long): Contact? = dao.getById(id)

    suspend fun save(contact: Contact) {
        if (contact.id == 0L) dao.insert(contact) else dao.update(contact)
    }

    suspend fun toggleFavorite(contact: Contact) =
        dao.update(contact.copy(isFavorite = !contact.isFavorite))

    suspend fun delete(contact: Contact) = dao.delete(contact)
}
