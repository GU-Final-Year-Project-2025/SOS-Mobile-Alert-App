package com.example.todoapp

import android.app.Application

class AppContext: Application () {
    companion object {
        lateinit var appContext: AppContext
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}