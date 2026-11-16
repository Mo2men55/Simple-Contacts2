package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * Data Access Object (DAO) for Room Database.
 * Contains methods to query, insert, update, and delete contacts.
 */
@Dao
interface ContactDao {

    @Query("SELECT * FROM contacts ORDER BY isFavorite DESC, name COLLATE NOCASE ASC")
    suspend fun getAllContacts(): List<Contact>

    @Query(
        """
        SELECT * FROM contacts
        WHERE name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%'
        ORDER BY isFavorite DESC, name COLLATE NOCASE ASC
        """
    )
    suspend fun searchContacts(query: String): List<Contact>

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: Long): Contact?

    @Insert
    suspend fun insert(contact: Contact): Long

    @Update
    suspend fun update(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)
}
