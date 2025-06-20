package com.example.todoapp.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

// AppSettings Entity
@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "profile_id") val profileId: Int,
    @ColumnInfo(name = "power_button_press") val powerButtonPress: Int,
    @ColumnInfo(name = "countdown_seconds") val countdownSeconds: Int,
    @ColumnInfo(name = "max_recipient") val maxRecipient: Int,
    @ColumnInfo(name = "send_location") val sendLocation: Boolean,
    @ColumnInfo(name = "auto_record_audio") val autoRecordAudio: Boolean,
    @ColumnInfo(name = "auto_record_video") val autoRecordVideo: Boolean,
    @ColumnInfo(name = "date_created") val dateCreated: Date,
    @ColumnInfo(name = "created_by") val createdBy: String,
    @ColumnInfo(name = "date_modified") val dateModified: Date,
    @ColumnInfo(name = "modified_by") val modifiedBy: String
)