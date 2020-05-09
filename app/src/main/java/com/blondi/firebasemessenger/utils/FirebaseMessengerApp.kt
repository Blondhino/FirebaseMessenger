package com.blondi.firebasemessenger.utils

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class FirebaseMessengerApp : Application() {
    companion object {
        lateinit var instance: FirebaseMessengerApp
            private set
        fun getAppContext(): Context= instance.applicationContext

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

    }

}