package com.example.outfitcoordination.Model

import android.media.Image

data class Clothes(
    val color : String = "",
    var favourite : Boolean = false,
    var public : Boolean = false,
    val image: String = "",
    val female : String = "",
    val male : String = "",
    var id : String ="",
    val type : String = "",
    val name : String = ""
)