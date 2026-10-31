package com.example.app

import android.app.Application
import android.util.Log
import com.example.core.di.AppContainer
import com.google.firebase.FirebaseApp

class ServiXApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("ServiXApplication", "FirebaseApp initialized in Application.onCreate")
            }
            AppContainer.init(this)
        } catch (e: Exception) {
            Log.e("ServiXApplication", "Failed to initialize FirebaseApp in Application.onCreate", e)
            AppContainer.useMockRepositories()
        }
    }
}
