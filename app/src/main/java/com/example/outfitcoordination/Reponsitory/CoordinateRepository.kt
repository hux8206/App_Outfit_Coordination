package com.example.outfitcoordination.Reponsitory

import android.util.Log
import com.example.outfitcoordination.Model.Clothes
import com.example.outfitcoordination.Model.ComboBoxInput
import com.example.outfitcoordination.Model.OutfitItem
import com.example.outfitcoordination.Model.OutfitUIModel
import com.example.outfitcoordination.Model.TextInput
import com.example.outfitcoordination.Network.RetrofitClient
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CoordinateRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun predictByComboBox(
        input : ComboBoxInput
    ) : List<OutfitUIModel>{  //vi sao lai co kieu list
        val response = RetrofitClient.api.predict(input)
        Log.d(
            "PREDICT",
            response.toString()
        )
        return mapOutfit(response.outfits,input.sex)
    }

    suspend fun predictByText(
        text : String //vi sao k truyen data class nhu combbox
    ) : List<OutfitUIModel>{
        val response = RetrofitClient.api.predictText(TextInput(text))
        Log.d("PREDICT_TEXT", response.toString())
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
            Log.d("PREDICT", "aoTrong image: ${aoTrong?.image}")
            Log.d("PREDICT", "aoKhoac image: ${aoKhoac?.image}")
            Log.d("PREDICT", "quan image: ${quan?.image}")

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
    private fun isNoJacket(aoKhoac : String): Boolean{
        val normalized = aoKhoac.trim().lowercase()
        return normalized.isBlank() || normalized == "khong_co"
    }
}