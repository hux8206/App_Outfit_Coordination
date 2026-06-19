package com.example.outfitcoordination.Repository

import com.google.firebase.firestore.FirebaseFirestore

class AdminRepository {
    private val db = FirebaseFirestore.getInstance()
    fun getStatistics(onResult: (Int, Int, Int) -> Unit) {
        var userCount = 0
        var clothesCount = 0
        var outfitCount = 0

        db.collection("users").get().addOnSuccessListener { users ->
            userCount = users.size()
            db.collection("clothes").get().addOnSuccessListener { clothes ->
                clothesCount = clothes.size()
                db.collection("outfits").get().addOnSuccessListener { outfits ->
                    outfitCount = outfits.size()
                    onResult(userCount, clothesCount, outfitCount)
                }
            }
        }
    }
}