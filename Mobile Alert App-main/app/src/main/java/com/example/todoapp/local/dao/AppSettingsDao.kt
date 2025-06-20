package com.example.todoapp.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.local.entities.AppSettingsEntity

// AppSettings DAO
@Dao
interface AppSettingsDao {
    @Insert
    fun insert(appSettings: AppSettingsEntity)

    @Update
    fun update(appSettings: AppSettingsEntity)

    @Query("SELECT * FROM app_settings WHERE profile_id = :profileId")
    fun getAppSettingsByProfileId(profileId: Int): AppSettingsEntity?
}
