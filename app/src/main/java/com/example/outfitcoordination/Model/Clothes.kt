package com.example.outfitcoordination.Model

import android.media.Image

data class Clothes(
    val color : String = "",
    val favourite : Boolean = false,
    val public : Boolean = false,
    val image: String = "",
    val female : String = "",
    val male : String = "",
    val id : String ="",
    val type : String = "",
    val name : String = ""
)