package com.example.outfitcoordination.Network

import com.example.outfitcoordination.Model.OutfitResult
import com.example.outfitcoordination.Model.UserInput
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService{
    @POST("/predict")
    suspend fun predict(@Body input: UserInput): OutfitResult //object UserInput se duoc convert thanh json sau do gui len sever
}
