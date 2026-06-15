package com.example.outfitcoordination

import android.graphics.Color

object DisplayFormatter {
    fun clothesName(value: String): String {
        return when (value) {
            "ao_thun" -> "Áo thun"
            "ao_len" -> "Áo len"
            "so_mi" -> "Sơ mi"
            "polo" -> "Áo polo"
            "tank_top" -> "Áo tank top"
            "crop_top" -> "Áo croptop"

            "jacket" -> "Áo khoác"
            "ao_chong_nang" -> "Áo chống nắng"
            "hoodie" -> "Hoodie"
            "blazer" -> "Blazer"
            "cardigan" -> "Cardigan"
            "khong_co" -> "Không có"

            "jeans" -> "Quần jeans"
            "kaki" -> "Quần kaki"
            "quan_tay" -> "Quần tây"
            "quan_ni" -> "Quần nỉ"
            "quan_ong_suong" -> "Quần ống suông"
            "short" -> "Quần short"
            "chan_vay" -> "Chân váy"

            "trang" -> "trắng"
            "den" -> "đen"
            "xam" -> "xám"
            "xanh" -> "xanh"
            "nau" -> "nâu"
            "xanhla" -> "xanh lá"
            "kem" -> "kem"
            "vang" -> "vàng"
            "hong" -> "hồng"
            "do" -> "đỏ"
            "cam" -> "cam"
            "be" -> "be"

            else -> value
                .replace("_", " ")
                .replaceFirstChar { it.uppercase() }
        }
    }

    fun getColor(value: String): Int {
        return when (value) {
            "trang" -> Color.WHITE
            "den" -> Color.BLACK
            "xam" -> Color.GRAY
            "xanh" -> Color.rgb(0, 51, 102)
            "nau" -> Color.rgb(121, 85, 72)
            "xanhla" -> Color.rgb(28, 102, 28)
            "kem" -> Color.rgb(250, 223, 180)
            "vang" -> Color.YELLOW
            "hong" -> Color.rgb(255, 192, 203)
            "do" -> Color.RED
            "cam" -> Color.rgb(255, 152, 0)
            "be" -> Color.rgb(232, 224, 213)
            else -> Color.LTGRAY
        }
    }
}