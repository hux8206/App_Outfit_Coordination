package com.example.outfitcoordination.Reponsitory

import com.example.outfitcoordination.Model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val authen = Firebase.auth
    private val db = Firebase.firestore

    suspend fun register(
        user: User,
        password: String
    ): Result<Boolean> {
        return try {
            val result = authen.createUserWithEmailAndPassword(user.email, password).await()
            val userID = result.user?.uid ?: return Result.failure(Exception("Không lấy được UID"))
            val firebaseUser = result.user ?: return Result.failure(Exception("User null"))
            val profileUpdates = userProfileChangeRequest {
                    displayName = user.name
            }
            firebaseUser.updateProfile(profileUpdates).await()
            db.collection("users")
                .document(userID)
                .set(user)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Result<Boolean> {
        return try {
            authen.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}