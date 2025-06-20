package com.example.todoapp.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.todoapp.local.DateTypeConverter
import com.example.todoapp.local.dao.AppSettingsDao
import com.example.todoapp.local.dao.ContactDao
import com.example.todoapp.local.dao.HistoryDao
import com.example.todoapp.local.dao.MedicalUnitsDao
import com.example.todoapp.local.dao.MessageMediaDao
import com.example.todoapp.local.dao.MessageRecipientDao
import com.example.todoapp.local.dao.SOSMessageDao
import com.example.todoapp.local.dao.TimerDao
import com.example.todoapp.local.dao.UserDao
import com.example.todoapp.local.dao.VideoCaptureDao
import com.example.todoapp.local.entities.AppSettingsEntity
import com.example.todoapp.local.entities.ContactEntity
import com.example.todoapp.local.entities.HistoryEntity
import com.example.todoapp.local.entities.MedicalUnitsEntity
import com.example.todoapp.local.entities.MessageMediaEntity
import com.example.todoapp.local.entities.MessageRecipientEntity
import com.example.todoapp.local.entities.SOSMessageEntity
import com.example.todoapp.local.entities.TimerEntity
import com.example.todoapp.local.entities.UserEntity
import com.example.todoapp.local.entities.VideoCapture

// Main Database Class
@Database(
    entities = [
        AppSettingsEntity::class,
        UserEntity::class,
        ContactEntity::class,
        SOSMessageEntity::class,
        MessageMediaEntity::class,
        MessageRecipientEntity::class,
        TimerEntity::class,
        HistoryEntity::class,
        VideoCapture::class,
        MedicalUnitsEntity::class
    ],
    version = 1
)
@TypeConverters(DateTypeConverter::class)
abstract class MyAppDatabase : RoomDatabase() {
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun userDao(): UserDao
    abstract fun contactDao(): ContactDao
    abstract fun sosMessageDao(): SOSMessageDao
    abstract fun messageMediaDao(): MessageMediaDao
    abstract fun messageRecipientDao(): MessageRecipientDao
    abstract fun timerDao(): TimerDao
    abstract fun historyDao(): HistoryDao
    abstract fun videoCaptureDao(): VideoCaptureDao
    abstract fun medicalUnitsDao(): MedicalUnitsDao

    companion object {
        @Volatile
        private var INSTANCE: MyAppDatabase? = null

        fun getDatabase(context: Context): MyAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyAppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}