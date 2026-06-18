package com.example.outfitcoordination.Repository

import com.example.outfitcoordination.Model.User
import com.google.firebase.firestore.FirebaseFirestore

class ManageUserRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getAllUsers(onSuccess: (List<User>) -> Unit, onFailure: (String) -> Unit) {
        db.collection("users").get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<User>()
                for (doc in result) {
                    val user = doc.toObject(User::class.java).apply { id = doc.id }
                    list.add(user)
                }
                onSuccess(list)
            }
            .addOnFailureListener { onFailure("Lỗi tải dữ liệu") }
    }

    fun deleteUser(userId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        db.collection("users").document(userId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }

    fun updateUserState(userId: String, newState: Int, onSuccess: () -> Unit, onFailure: () -> Unit) {
        db.collection("users").document(userId).update("state", newState)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }
}