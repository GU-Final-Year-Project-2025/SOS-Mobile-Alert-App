package com.example.todoapp.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medical_units")
data class MedicalUnitsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "medical_unit_name") val medical_unit_name: String,
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "phone_number") val phone_number: String,
    @ColumnInfo(name = "is_active") val is_active: Boolean = true
)