package com.example.quadrilateralquest

import android.app.Application
import com.google.firebase.FirebaseApp

class QuadrilateralQuestApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        // FirebaseApp.initializeApp(this)
        // Note: FirebaseApp.initializeApp is automatically handled by the ContentProvider from firebase-common,
        // but explicit call is fine too if we had the config.
        // Since we are mocking google-services.json, we might expect a crash if we try to actually use it without valid config.
        // We will mock the json next.
    }
}
