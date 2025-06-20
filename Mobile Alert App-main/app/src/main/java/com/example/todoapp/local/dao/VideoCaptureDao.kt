package com.example.todoapp.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.local.entities.VideoCapture
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoCaptureDao {
    @Insert
    fun insert(videoCapture: VideoCapture)

    @Update
    fun update(videoCapture: VideoCapture)

    @Query("SELECT * FROM videocapture")
    fun getAllVideoCaptures(): Flow<List<VideoCapture>>
}