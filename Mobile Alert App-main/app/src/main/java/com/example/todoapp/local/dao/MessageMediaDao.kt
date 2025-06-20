package com.example.todoapp.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.local.entities.MessageMediaEntity

// Message Media DAO
@Dao
interface MessageMediaDao {
    @Insert
    fun insert(messageMedia: MessageMediaEntity)

    @Update
    fun update(messageMedia: MessageMediaEntity)

    @Query("SELECT * FROM message_media WHERE message_id = :messageId")
    fun getMessageMediaByMessageId(messageId: Int): List<MessageMediaEntity>
}