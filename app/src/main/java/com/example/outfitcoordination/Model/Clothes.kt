package com.example.outfitcoordination.Model

import android.media.Image

data class Clothes(
    val color : String = "",
    val favor : Boolean = false,
    val image: String = "",
    val linkFemale : String = "",
    val linkMale : String = "",
    val publicID : String ="",
    val type : String = ""
)