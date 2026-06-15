package com.example.outfitcoordination.Model
data class OutfitUIModel(
    val aoTrongImage: String = "",
    val aoKhoacImage: String = "",
    val quanImage: String = "",

    val aoTrongName: String = "",
    val aoKhoacName: String = "",
    val quanName: String = "",

    val mauAoTrong: String = "",
    val mauAoKhoac: String = "",
    val mauQuan: String = "",

    val hasAoKhoac: Boolean = true,
    val compatibility: Double = 0.0,

    val aoTrongLink: String = "",
    val aoKhoacLink: String = "",
    val quanLink: String = "",

    val userId: String = "",
    val userName: String = "",

    val public: Boolean = false,
    val favorite: Boolean = false,
    var outfitID : String = ""
)
