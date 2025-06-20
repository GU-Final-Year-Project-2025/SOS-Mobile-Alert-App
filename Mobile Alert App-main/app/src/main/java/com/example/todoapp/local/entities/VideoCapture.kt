package com.example.todoapp.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "videocapture")
data class VideoCapture(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "user_id") val user_id: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "is_sent") val status: Boolean,
    @ColumnInfo(name = "date_created") val dateTime: Date,
)