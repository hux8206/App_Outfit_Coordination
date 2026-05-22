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

    suspend fun register(user : User, password : String) : Boolean{ //nhan du lieu ViewModel
        return try{
            val result = authen.createUserWithEmailAndPassword(user.email, password).await()
            val userID = result.user?.uid ?: return false //neu uid null return false
            val firebaseUser = result.user ?: return false
            val profileUpdates = userProfileChangeRequest {
                displayName = user.name
            }
            firebaseUser.updateProfile(profileUpdates).await()

            db.collection("users") //tao document
                .document(userID)
                .set(user)
                .await()
            true
        }catch(e : Exception){
            false
        }
    }

    suspend fun login(email : String, password : String): Boolean{
        return try{
            authen.signInWithEmailAndPassword(email, password).await()
            true
        }catch(e : Exception){
            false
        }
    }
}