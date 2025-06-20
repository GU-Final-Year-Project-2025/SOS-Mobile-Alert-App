package com.example.todoapp.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

// Message Media Entity
@Entity(
    tableName = "message_media",
    foreignKeys = [
        ForeignKey(
            entity = SOSMessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["message_id"]
        )
    ]
)
data class MessageMediaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "message_id") val messageId: Int,
    @ColumnInfo(name = "media_type") val mediaType: String,
    @ColumnInfo(name = "media_url") val mediaUrl: String,
    @ColumnInfo(name = "duration_seconds") val durationSeconds: Int,
    @ColumnInfo(name = "date_created") val dateCreated: Date,
    @ColumnInfo(name = "created_by") val createdBy: String,
    @ColumnInfo(name = "date_modified") val dateModified: Date,
    @ColumnInfo(name = "modified_by") val modifiedBy: String
)