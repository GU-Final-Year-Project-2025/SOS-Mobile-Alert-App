package com.example.todoapp.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.local.entities.ContactEntity
import com.example.todoapp.local.entities.MedicalUnitsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicalUnitsDao {
    @Insert
    fun insertMedicalUnit(medicalUnit: MedicalUnitsEntity)

    @Update
    fun updateMedicalUnit(medicalUnit: MedicalUnitsEntity)

    @Query("SELECT * FROM medical_units")
    fun getAllMedicalUnits(): Flow<List<MedicalUnitsEntity>>

    @Delete
    fun deleteMedicalUnits(medicalUnit: MedicalUnitsEntity)
}