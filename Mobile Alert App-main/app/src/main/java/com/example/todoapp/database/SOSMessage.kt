package com.example.todoapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class SOSMessage(
    @PrimaryKey(autoGenerate = true) val uuid: Int = 0,
    @ColumnInfo(name = "message") val message: String
)