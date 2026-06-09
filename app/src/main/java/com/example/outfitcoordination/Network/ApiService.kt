package com.example.outfitcoordination.Network

import com.example.outfitcoordination.Model.ComboBoxInput
import com.example.outfitcoordination.Model.OutfitsResponse
import com.example.outfitcoordination.Model.TextInput
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService{
    @POST("/predict_text")
    suspend fun predictText(@Body input: TextInput): OutfitsResponse//object UserInput se duoc convert thanh json sau do gui len sever

    @POST("/predict")
    suspend fun predict(@Body input : ComboBoxInput): OutfitsResponse
}
