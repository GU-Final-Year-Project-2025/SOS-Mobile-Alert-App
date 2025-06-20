package com.example.todoapp.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

// Timer Entity
@Entity(
    tableName = "timers",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"]
        )
    ]
)
data class TimerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "minutes") val minutes: Int,
    @ColumnInfo(name = "seconds") val seconds: Int,
    @ColumnInfo(name = "date_created") val dateCreated: Date,
    @ColumnInfo(name = "created_by") val createdBy: String,
    @ColumnInfo(name = "date_modified") val dateModified: Date,
    @ColumnInfo(name = "modified_by") val modifiedBy: String
)