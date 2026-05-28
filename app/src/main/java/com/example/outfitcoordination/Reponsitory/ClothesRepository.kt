package com.example.outfitcoordination.Reponsitory
import com.example.outfitcoordination.Model.Clothes
import com.google.firebase.firestore.FirebaseFirestore

class ClothesRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getClothes(
        onResult : (List<Clothes>) -> Unit
    ){
        db.collection("clothes")
            .get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<Clothes>()
                for (doc in result){
                    val item = doc.toObject(Clothes::class.java)
                    list.add(item)
                }
                onResult(list)
            }
    }
}