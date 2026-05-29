package com.example.outfitcoordination.Reponsitory
import android.widget.Toast
import com.example.outfitcoordination.Model.Clothes
import com.example.outfitcoordination.View.Favorites
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
                    item.id = doc.id
                    list.add(item)
                }
                onResult(list)
            }
    }

    fun updateFavor(id:String, isFavor : Boolean, onComplete : (Boolean) -> Unit){
        db.collection("clothes")
            .document(id)
            .update("favourite",isFavor)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
}