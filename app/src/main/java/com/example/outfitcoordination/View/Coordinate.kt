package com.example.outfitcoordination.View

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.outfitcoordination.Model.Clothes
import com.example.outfitcoordination.Model.UserInput
import com.example.outfitcoordination.Network.RetrofitClient
import com.example.outfitcoordination.R
import com.example.outfitcoordination.databinding.FragmentCoordinateBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class Coordinate : Fragment() {

    private var _binding: FragmentCoordinateBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private var selectedShopLink = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoordinateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupDropdowns()
        hidePredictTexts()
        resetLinkButtons()

        binding.btnGenerate.setOnClickListener {
            generateOutfit()
        }

        binding.btnMaleLink.setOnClickListener {
            openShopLink()
        }

        binding.btnFemaleLink.setOnClickListener {
            openShopLink()
        }
    }

    private fun setupDropdowns() {
        binding.dropdownSkin.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                listOf("trang", "ngam", "vang"))
        )

        binding.dropdownSeason.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                listOf("nong", "lanh", "mat"))
        )

        binding.dropdownSex.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                listOf("nam", "nu"))
        )

        binding.dropdownSituation.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                listOf("di_hoc", "di_choi", "di_lam"))
        )

        binding.dropdownStyle.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                listOf("toi_gian", "streetwear", "lich_su", "sporty", "han_quoc"))
        )
    }

    private fun generateOutfit() {
        val input = UserInput(
            skin = binding.dropdownSkin.text.toString().trim(),
            season = binding.dropdownSeason.text.toString().trim(),
            sex = binding.dropdownSex.text.toString().trim(),
            situation = binding.dropdownSituation.text.toString().trim(),
            style = binding.dropdownStyle.text.toString().trim()
        )

        if (input.skin.isBlank() || input.season.isBlank() || input.sex.isBlank()
            || input.situation.isBlank() || input.style.isBlank()
        ) {
            Toast.makeText(requireContext(), "Vui lòng chọn đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                binding.progressLoading.visibility = View.VISIBLE
                binding.layoutResult.visibility = View.GONE
                resetLinkButtons()
                hidePredictTexts()

                val result = RetrofitClient.api.predict(input)

                val aoTrong = findClothes(result.ao_trong, result.mau_ao_trong)
                val aoKhoac = if (isNoJacket(result.ao_khoac)) {
                    null
                } else {
                    findClothes(result.ao_khoac, result.mau_ao_khoac)
                }
                val quan = findClothes(result.quan, result.mau_quan)

                loadImage(aoTrong?.image, binding.imgAoTrong)
                loadImage(quan?.image, binding.imgQuan)

                if (aoKhoac == null) {
                    hideJacketView()
                } else {
                    showJacketView()
                    loadImage(aoKhoac.image, binding.imgAoKhoac)
                }

                selectedShopLink = getShopLinkBySex(input.sex, aoTrong, aoKhoac, quan)
                setupLinkButton(input.sex)

                saveOutfitToFirestore(
                    sex = input.sex,
                    aoTrong = aoTrong,
                    aoKhoac = aoKhoac,
                    quan = quan,
                    shopLink = selectedShopLink
                )

                binding.layoutResult.visibility = View.VISIBLE

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressLoading.visibility = View.GONE
            }
        }
    }

    private fun isNoJacket(value: String): Boolean {
        val normalized = value.trim().lowercase()
        return normalized.isBlank()
                || normalized == "khong"
                || normalized == "khong_co"
                || normalized == "khong co"
                || normalized == "không"
                || normalized == "không có"
                || normalized == "none"
                || normalized == "null"
    }

    private suspend fun findClothes(type: String, color: String): Clothes? {
        val cleanType = type.trim()
        val cleanColor = color.trim()

        if (cleanType.isBlank()) return null

        val fallbackColors = listOf(
            cleanColor,
            normalizeColor(cleanColor),
            "#FFFFFF"
        ).distinct().filter { it.isNotBlank() }

        for (fallbackColor in fallbackColors) {
            val snapshot = db.collection("clothes")
                .whereEqualTo("type", cleanType)
                .whereEqualTo("color", fallbackColor)
                .limit(1)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                return snapshot.documents.firstOrNull()?.toObject(Clothes::class.java)
            }
        }

        val sameTypeSnapshot = db.collection("clothes")
            .whereEqualTo("type", cleanType)
            .limit(1)
            .get()
            .await()

        return sameTypeSnapshot.documents.firstOrNull()?.toObject(Clothes::class.java)
    }

    private fun normalizeColor(color: String): String {
        return when (color.trim().lowercase()) {
            "trắng" -> "trang"
            "trang" -> "trắng"
            "đen" -> "den"
            "den" -> "đen"
            "white" -> "trang"
            "black" -> "den"
            else -> color.trim()
        }
    }

    private fun getShopLinkBySex(
        sex: String,
        aoTrong: Clothes?,
        aoKhoac: Clothes?,
        quan: Clothes?
    ): String {
        return if (sex == "nam") {
            aoTrong?.linkMale?.takeIf { it.isNotBlank() }
                ?: aoKhoac?.linkMale?.takeIf { it.isNotBlank() }
                ?: quan?.linkMale?.takeIf { it.isNotBlank() }
                ?: ""
        } else {
            aoTrong?.linkFemale?.takeIf { it.isNotBlank() }
                ?: aoKhoac?.linkFemale?.takeIf { it.isNotBlank() }
                ?: quan?.linkFemale?.takeIf { it.isNotBlank() }
                ?: ""
        }
    }

    private fun setupLinkButton(sex: String) {
        binding.btnMaleLink.visibility = View.GONE
        binding.btnFemaleLink.visibility = View.GONE

        if (selectedShopLink.isBlank()) return

        if (sex == "nam") {
            binding.btnMaleLink.visibility = View.VISIBLE
            binding.btnMaleLink.text = "Xem link mua đồ"
        } else {
            binding.btnFemaleLink.visibility = View.VISIBLE
            binding.btnFemaleLink.text = "Xem link mua đồ"
        }
    }

    private fun resetLinkButtons() {
        selectedShopLink = ""
        binding.btnMaleLink.visibility = View.GONE
        binding.btnFemaleLink.visibility = View.GONE
    }

    private fun openShopLink() {
        if (selectedShopLink.isBlank()) {
            Toast.makeText(requireContext(), "Chưa có link mua đồ", Toast.LENGTH_SHORT).show()
            return
        }

        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(selectedShopLink)))
    }

    private suspend fun saveOutfitToFirestore(
        sex: String,
        aoTrong: Clothes?,
        aoKhoac: Clothes?,
        quan: Clothes?,
        shopLink: String
    ) {
        val outfit = hashMapOf(
            "aoTrongImage" to (aoTrong?.image ?: ""),
            "aoKhoacImage" to (aoKhoac?.image ?: ""),
            "quanImage" to (quan?.image ?: ""),
            "hasAoKhoac" to (aoKhoac != null),
            "sex" to sex,
            "shopLink" to shopLink,
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("outfits").add(outfit).await()
    }

    private fun loadImage(url: String?, imageView: ImageView) {
        Glide.with(requireContext())
            .load(url)
            .placeholder(R.drawable.fashion_banner)
            .error(R.drawable.fashion_banner)
            .into(imageView)
    }

    private fun hideJacketView() {
        binding.imgAoKhoac.visibility = View.GONE
        val parent = binding.imgAoKhoac.parent
        if (parent is View) parent.visibility = View.GONE
    }

    private fun showJacketView() {
        val parent = binding.imgAoKhoac.parent
        if (parent is View) parent.visibility = View.VISIBLE
        binding.imgAoKhoac.visibility = View.VISIBLE
    }

    private fun hidePredictTexts() {
        binding.tvResultInfo.visibility = View.GONE
        binding.tvAoTrongInfo.visibility = View.GONE
        binding.tvAoKhoacInfo.visibility = View.GONE
        binding.tvQuanInfo.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}