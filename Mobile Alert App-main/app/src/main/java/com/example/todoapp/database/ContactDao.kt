package com.example.todoapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface ContactDao {

    // Get all contacts
    @Query("SELECT * FROM contact")
    fun getAllContacts(): Flow<List<Contact>>

    // Get a contact by ID
    @Query("SELECT * FROM contact WHERE uid = :contactId")
    fun getContactById(contactId: Int): Contact

    // Insert a new contact
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addContact(contact: Contact)

    // Update contact details
    @Update
    suspend fun updateContact(contact: Contact)

    // Delete a specific contact
    @Delete
    suspend fun deleteContact(contact: Contact)


}