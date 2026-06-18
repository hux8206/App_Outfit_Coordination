package com.example.outfitcoordination.Reponsitory

import android.util.Log
import com.example.outfitcoordination.Model.Clothes
import com.example.outfitcoordination.Model.ComboBoxInput
import com.example.outfitcoordination.Model.OutfitItem
import com.example.outfitcoordination.Model.OutfitUIModel
import com.example.outfitcoordination.Model.TextInput
import com.example.outfitcoordination.Network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CoordinateRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    suspend fun predictByComboBox(
        input : ComboBoxInput
    ) : List<OutfitUIModel>{  //vi sao lai co kieu list
        val response = RetrofitClient.api.predict(input)
        return mapOutfit(response.outfits,input.sex)
    }

    suspend fun predictByText(
        text : String //vi sao k truyen data class nhu combbox
    ) : List<OutfitUIModel>{
        val response = RetrofitClient.api.predictText(TextInput(text))
        return mapOutfit(response.outfits ?: emptyList(),sex = "")
    }

    private suspend fun mapOutfit(
        outfits : List<OutfitItem>,
        sex : String
    ) : List<OutfitUIModel>{
        return outfits.map { item ->
            val aoTrong = findClothes(item.ao_trong, "ao_trong", item.mau_ao_trong)
            val aoKhoac =
                if (isNoJacket(item.ao_khoac)){
                    null
                }else{
                    findClothes(item.ao_khoac, "ao_khoac", item.mau_ao_khoac)
                }
            val quan = findClothes(item.quan, "quan", item.mau_quan)

            OutfitUIModel(
                aoTrongImage = aoTrong?.image ?: "",
                aoKhoacImage = aoKhoac?.image ?: "",
                quanImage = quan?.image ?: "",

                aoTrongName = item.ao_trong,
                aoKhoacName = item.ao_khoac,
                quanName = item.quan,

                mauAoTrong = item.mau_ao_trong,
                mauAoKhoac = item.mau_ao_khoac,
                mauQuan = item.mau_quan,

                hasAoKhoac = aoKhoac != null,
                compatibility = item.compatibility,

                aoTrongLink = getLink(sex, aoTrong),
                aoKhoacLink = getLink(sex, aoKhoac),
                quanLink = getLink(sex, quan)
            )
        }
    }

    private suspend fun findClothes(
        name: String,
        type: String,
        color: String
    ): Clothes?{
        val snapshot = db.collection("clothes")
            .whereEqualTo("name",name)
            .whereEqualTo("type",type)
            .whereEqualTo("color",color)
            .limit(1)
            .get()
            .await()

        if (!snapshot.isEmpty){
            return snapshot.documents.firstOrNull()?.toObject(Clothes::class.java)
        }

        return null
    }

    fun getLink(
        sex: String,
        clothes: Clothes?
    ): String {

        if (clothes == null) {
            return ""
        }
        if (sex == "nu") {
            if (clothes.female.isNotBlank()) {
                return clothes.female
            }
        } else {
            if (clothes.male.isNotBlank()) {
                return clothes.male
            }
        }
        return ""
    }
    private fun isNoJacket(aoKhoac : String): Boolean {
        val normalized = aoKhoac.trim().lowercase()
        return normalized.isBlank() || normalized == "khong_co"
    }

    suspend fun saveOutfit(outfit: OutfitUIModel): String {
        val user = auth.currentUser
        val uid = user?.uid ?: return ""
        val userName = user?.displayName

        val outfitRef = db.collection("outfits").document()
        val outfitId = outfitRef.id

        val saved = outfit.copy(
            userId = uid,
            favorite = true,
            public = false,
            userName = userName ?: "",
            outfitID = outfitId
        )

        db.collection("outfits")
            .document(outfitId)
            .set(saved)
            .await()

        return outfitId
    }

    suspend fun deleteOutfit(outfitId: String) {
        if (outfitId.isBlank()){
            return
        }
        db.collection("outfits")
            .document(outfitId)
            .delete()
            .await()
    }

    suspend fun toggleFavoriteOutfit(
        outfit: OutfitUIModel
    ): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        val favId = uid + "_" + outfit.outfitID
        val favRef = db.collection("favorite_outfits").document(favId)
        if (!outfit.favorite) {
            favRef.set(
                mapOf(
                    "userId" to uid,
                    "outfitID" to outfit.outfitID
                )
            ).await()
        } else {
            favRef.delete().await()
        }
        return true
    }

    suspend fun getFavorOutfit(): List<OutfitUIModel> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        val favSnapshot = db.collection("favorite_outfits")
            .whereEqualTo("userId", uid)
            .get()
            .await()
        val outfitIds = favSnapshot.documents.mapNotNull { it.getString("outfitID") }
        val result = mutableListOf<OutfitUIModel>()

        for (id in outfitIds) {
            if (id.isNotBlank()) {
                try {
                    val doc = db.collection("outfits")
                        .document(id)
                        .get()
                        .await()
                    val outfit = doc.toObject(OutfitUIModel::class.java)
                    if (outfit != null) {
                        result.add(
                            outfit.copy(
                                outfitID = doc.id,
                                favorite = true
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.e("FirebaseError", "Lỗi lấy outfit yêu thích ID: $id", e)
                }
            }
        }
        return result
    }

    suspend fun updataSwitchOutfit(
        outfitID : String,
        isPublic : Boolean
    ){
        if (outfitID.isBlank()) return
        db.collection("outfits")
            .document(outfitID)
            .update("public",isPublic)
            .await()
    }

    suspend fun getOutfitPublic() : List<OutfitUIModel>{
        val uid = auth.currentUser?.uid ?: return emptyList()
        val snapshot = db.collection("outfits")
            .whereEqualTo("public", true)
            .get()
            .await()

        val publicOutfits = snapshot.documents.mapNotNull { doc ->
            val outfit = doc.toObject(OutfitUIModel::class.java)
            outfit?.copy(outfitID = doc.id)
        }
        val favSnapshot = db.collection("favorite_outfits")
            .whereEqualTo("userId", uid)
            .get()
            .await()
        val myFavOutfitIds = favSnapshot.documents.mapNotNull { it.getString("outfitID") }.toSet()

        return publicOutfits.map { outfit ->
            outfit.copy(favorite = myFavOutfitIds.contains(outfit.outfitID))
        }
    }
}