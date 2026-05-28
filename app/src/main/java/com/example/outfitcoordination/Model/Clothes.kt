package com.example.outfitcoordination.Model

import android.media.Image

data class Clothes(
    val color : String = "",
    val favorite : Boolean = false,
    val public : Boolean = false,
    val image: String = "",
    val linkFemale : String = "",
    val linkMale : String = "",
    val publicID : String ="",
    val type : String = "",
    val name : String = ""
)