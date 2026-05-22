package com.example.outfitcoordination.Network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://apioutfit-coordination-production.up.railway.app"
    val api : ApiService by lazy { //bien api voi kieu du lieu ApiServer se duoc dung method predict trong do, by lazy dung de tao lan dau va duoc tai su dung khi can, vi du chi khi bam nut button no se goi retrofit, va cac lan sau bam button se lay ra k can goi lai, nhung khi tat app thi se mat
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // gson convert du lieu tra ve cua sever duoi dang json thanh OutfitResult
            .build()
            .create(ApiService::class.java)
    }
}