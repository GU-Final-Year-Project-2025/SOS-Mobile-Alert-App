package com.example.todoapp.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.local.entities.MessageRecipientEntity

// Message Recipient DAO
@Dao
interface MessageRecipientDao {
    @Insert
    fun insert(messageRecipient: MessageRecipientEntity)

    @Update
    fun update(messageRecipient: MessageRecipientEntity)

    @Query("SELECT * FROM message_recipients WHERE message_id = :messageId")
    fun getMessageRecipientsByMessageId(messageId: Int): List<MessageRecipientEntity>
}