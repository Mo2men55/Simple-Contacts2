package com.example.myapplication2.data

class ContactRepository(private val dao: ContactDao) {

    suspend fun searchContacts(query: String): List<Contact> = dao.searchContacts(query.trim())

    suspend fun getContact(id: Long): Contact? = dao.getContactById(id)

    suspend fun save(contact: Contact) {
        if (contact.id == 0L) dao.insert(contact) else dao.update(contact)
    }

    suspend fun toggleFavorite(contact: Contact) =
        dao.update(contact.copy(isFavorite = !contact.isFavorite))

    suspend fun delete(contact: Contact) = dao.delete(contact)
}
