package com.example.todoapp.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

// SOS Message Entity
@Entity(
    tableName = "sos_messages",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["profile_id"]
        )
    ]
)
data class SOSMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "profile_id") val profileId: Int,
    @ColumnInfo(name = "message_text") val messageText: String,
//    @ColumnInfo(name = "location_coordinates") val locationCoordinates: String,
//    @ColumnInfo(name = "is_delivered") val isDelivered: Boolean,
    @ColumnInfo(name = "date_created") val dateCreated: Date,
    @ColumnInfo(name = "created_by") val createdBy: String,
    @ColumnInfo(name = "date_modified") val dateModified: Date,
    @ColumnInfo(name = "modified_by") val modifiedBy: String
)