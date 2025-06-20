package com.example.todoapp.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.local.entities.SOSMessageEntity

// SOS Message DAO
@Dao
interface SOSMessageDao {
    @Insert
    fun insert(sosMessage: SOSMessageEntity)

    @Update
    fun update(sosMessage: SOSMessageEntity)

    @Query("SELECT * FROM sos_messages WHERE profile_id = :profileId")
    fun getSOSMessagesByProfileId(profileId: Int): List<SOSMessageEntity>
}
