package com.example.todoapp.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.local.entities.TimerEntity
import kotlinx.coroutines.flow.Flow

// Timer DAO
@Dao
interface TimerDao {

    @Query("SELECT * FROM timers")
    fun getAllTimers(): Flow<List<TimerEntity>>

    @Insert
    fun insert(timer: TimerEntity)

    @Update
    fun update(timer: TimerEntity)

    @Query("SELECT * FROM timers WHERE user_id = :userId")
    fun getTimersByUserId(userId: Int): List<TimerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(timer: TimerEntity)
}