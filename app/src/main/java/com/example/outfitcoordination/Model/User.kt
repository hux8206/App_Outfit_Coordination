package com.example.outfitcoordination.Model

data class User (
    var id: String = "", // Thêm dòng này để hứng ID từ Firebase
    val name : String = "",
    val email : String = "",
    val state : Int = 1,
    val role : String = "user"
)