package com.example.todoapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SOSMessageDao {

    @Query("SELECT * FROM sosmessage")
    fun getMessage(): Flow<List<SOSMessage>>

    @Update
    suspend fun editMessage(message: SOSMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createMessage(message: SOSMessage)

    @Delete
    suspend fun deleteMessage(message: SOSMessage)
}