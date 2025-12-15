package com.example.quadrilateralquest.data

import com.example.quadrilateralquest.logic.UserProgress
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// A wrapper to handle Firebase interactions.
// In a real app, this would use Dependency Injection.
class FirebaseRepository {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getUserName(): String? {
        return auth.currentUser?.displayName
    }

    fun saveProgress(progress: UserProgress) {
        val user = auth.currentUser ?: return
        val ref = database.getReference("users/${user.uid}/progress")
        ref.setValue(progress)
    }

    // Note: In the prototype sandbox without a real Firebase project,
    // these calls will likely fail or do nothing if the config is invalid.
    // The UI handles the "mock" login for navigation purposes.
}
