package com.example.todoapp.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.todoapp.local.entities.HistoryEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface HistoryDao {
    @Insert
    fun createHistory(historyEntity: HistoryEntity)

    @Query("SELECT * FROM history")
    fun getAllHistory(): Flow<List<HistoryEntity>>
}