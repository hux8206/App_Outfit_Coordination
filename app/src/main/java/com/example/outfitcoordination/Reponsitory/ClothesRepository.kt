package com.example.outfitcoordination.Reponsitory
import android.widget.Toast
import com.example.outfitcoordination.Model.Clothes
import com.example.outfitcoordination.View.Favorites
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ClothesRepository {
    private val db = FirebaseFirestore.getInstance()
    fun getClothes(onResult : (List<Clothes>) -> Unit){
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

    fun updateFavor(
        clothes: Clothes,
        onComplete: (Boolean) -> Unit
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val favorId = uid + "_" + clothes.id
        val favRef = db.collection("favorite_clothes").document(favorId)
        if (!clothes.favourite) {
            favRef.set(
                mapOf(
                    "userId" to uid,
                    "clothesId" to clothes.id
                )
            ).addOnSuccessListener {
                onComplete(true)
            }.addOnFailureListener {
                onComplete(false)
            }
        } else {
            favRef.delete()
                .addOnSuccessListener {
                    onComplete(true)
                }.addOnFailureListener {
                    onComplete(false)
                }
        }
    }

    fun getClothesForCurrentUser(onResult: (List<Clothes>) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("clothes")
            .get()
            .addOnSuccessListener { result ->
                val list = result.map { doc ->
                    val item = doc.toObject(Clothes::class.java)
                    item.id = doc.id
                    item
                }.toMutableList()
                db.collection("favorite_clothes")
                    .whereEqualTo("userId", uid)
                    .get()
                    .addOnSuccessListener { favResult ->
                        val favIds = favResult.documents
                            .mapNotNull { it.getString("clothesId") }
                            .toSet()
                        list.forEach { item ->
                            item.favourite = favIds.contains(item.id)
                        }
                        onResult(list)
                    }
            }
    }

    suspend fun updataSwitchOutfit(
        outfitID : String,
        isPublic : Boolean
    ){
        if (outfitID.isBlank()) return
        db.collection("clothes")
            .document(outfitID)
            .update("public",isPublic)
            .await()
    }

    suspend fun getClothesPublic() : List<Clothes>{
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return emptyList()
        val snapshot = db.collection("clothes")
            .whereEqualTo("public",true)
            .get()
            .await()
        val publicList = snapshot.documents.mapNotNull { doc ->
            val clothes = doc.toObject(Clothes::class.java)
            clothes?.id = doc.id
            clothes
        }
        val favSnapshot = db.collection("favorite_clothes")
            .whereEqualTo("userId", uid)
            .get()
            .await()
        val myFavIds = favSnapshot.documents.mapNotNull { it.getString("clothesId") }.toSet()
        publicList.forEach {
            it.favourite = myFavIds.contains(it.id)
        }
        return publicList
    }

    suspend fun getClothesFavor() : List<Clothes>{
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return emptyList()
        val snapshot = db.collection("clothes")
            .whereEqualTo("favourite",true)
            .whereEqualTo("userId",uid)
            .get()
            .await()
        return snapshot.documents.mapNotNull {doc ->
            val clothes = doc.toObject(Clothes::class.java)
            clothes?.copy(id = doc.id)
        }
    }
}