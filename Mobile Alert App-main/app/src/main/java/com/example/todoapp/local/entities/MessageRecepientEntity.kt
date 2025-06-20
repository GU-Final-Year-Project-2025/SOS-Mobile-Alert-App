package com.example.todoapp.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

// Message Recipient Entity
@Entity(
    tableName = "message_recipients",
    foreignKeys = [
        ForeignKey(
            entity = SOSMessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["message_id"]
        ),
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["id"],
            childColumns = ["contact_id"]
        )
    ]
)
data class MessageRecipientEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "message_id") val messageId: Int,
    @ColumnInfo(name = "contact_id") val contactId: Int,
    @ColumnInfo(name = "is_delivered") val isDelivered: Boolean,
    @ColumnInfo(name = "delivery_time") val deliveryTime: Date,
    @ColumnInfo(name = "date_created") val dateCreated: Date,
    @ColumnInfo(name = "created_by") val createdBy: String,
    @ColumnInfo(name = "date_modified") val dateModified: Date,
    @ColumnInfo(name = "modified_by") val modifiedBy: String
)