package com.example.todoapp.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

// User Entity
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "fullname") val fullName: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "password") val password: String?,
    @ColumnInfo(name = "date_created") val dateCreated: Date?,
    @ColumnInfo(name = "created_by") val createdBy: String?,
    @ColumnInfo(name = "date_modified") val dateModified: Date?,
    @ColumnInfo(name = "modified_by") val modifiedBy: String?
)