package com.example.todoapp.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "time") val time: Date,
    @ColumnInfo(name = "audioPath") val audioPath: String? = null
)