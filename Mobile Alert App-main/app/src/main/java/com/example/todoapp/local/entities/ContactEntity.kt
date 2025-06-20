package com.example.todoapp.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

// Contact Entity
@Entity(
    tableName = "contacts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"]
        )
    ]
)
data class ContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true,
    @ColumnInfo(name = "date_created") val dateCreated: Date = Date(), // Default to current date
    @ColumnInfo(name = "created_by") val createdBy: String = "System", // Default value
    @ColumnInfo(name = "date_modified") val dateModified: Date? = null, // Nullable
    @ColumnInfo(name = "modified_by") val modifiedBy: String? = null // Nullable
)