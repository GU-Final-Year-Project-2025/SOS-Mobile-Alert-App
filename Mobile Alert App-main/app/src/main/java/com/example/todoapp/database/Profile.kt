package com.example.todoapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Profile(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "name") val name: String = "Edit your name here",
    @ColumnInfo(name = "email") val email: String = "Edit your email here",
    @ColumnInfo(name = "contact") val contact: String = "Edit your contact here",
    @ColumnInfo(name = "address") val address: String = "Edit your address here"
)