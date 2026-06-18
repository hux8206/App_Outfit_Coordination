package com.example.outfitcoordination.Repository

import com.example.outfitcoordination.Model.Clothes
import com.google.firebase.firestore.FirebaseFirestore

class ManageClothesRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getAllClothes(onSuccess: (List<Clothes>) -> Unit, onFailure: (String) -> Unit) {
        db.collection("clothes").get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<Clothes>()
                for (doc in result) {
                    val item = doc.toObject(Clothes::class.java).apply { id = doc.id }
                    list.add(item)
                }
                onSuccess(list)
            }
            .addOnFailureListener { onFailure("Lỗi tải dữ liệu clothes") }
    }

    fun deleteClothes(clothesId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        db.collection("clothes").document(clothesId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }
}