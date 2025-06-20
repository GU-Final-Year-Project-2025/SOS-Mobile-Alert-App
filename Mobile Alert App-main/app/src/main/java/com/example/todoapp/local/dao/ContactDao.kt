package com.example.todoapp.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.local.entities.ContactEntity
import kotlinx.coroutines.flow.Flow

// Contact DAO
@Dao
interface ContactDao {
    @Insert
    fun insert(contact: ContactEntity)

    @Update
    fun update(contact: ContactEntity)

    @Query("SELECT * FROM contacts WHERE user_id = :userId")
    fun getContactsByUserId(userId: Int): Flow<List<ContactEntity>>

    @Delete
    fun deleteContacts(contact: ContactEntity)
}