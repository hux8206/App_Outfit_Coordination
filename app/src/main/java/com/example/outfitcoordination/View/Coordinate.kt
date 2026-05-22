package com.example.outfitcoordination.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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

    private var maleLink = ""
    private var femaleLink = ""

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

        binding.btnGenerate.setOnClickListener {
            generateOutfit()
        }

        binding.btnMaleLink.setOnClickListener {
            Toast.makeText(requireContext(), maleLink.ifEmpty { "Chưa có link nam" }, Toast.LENGTH_SHORT).show()
        }

        binding.btnFemaleLink.setOnClickListener {
            Toast.makeText(requireContext(), femaleLink.ifEmpty { "Chưa có link nữ" }, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDropdowns() {
        binding.dropdownSkin.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                listOf("trang", "ngam", "vang")
            )
        )

        binding.dropdownSeason.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                listOf("nong", "lanh", "mat")
            )
        )

        binding.dropdownSex.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                listOf("nam", "nu")
            )
        )

        binding.dropdownSituation.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                listOf("di_hoc", "di_choi", "di_lam")
            )
        )

        binding.dropdownStyle.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                listOf("toi_gian", "streetwear", "lich_su", "sporty","han_quoc")
            )
        )
    }

    private fun generateOutfit() {
        val input = UserInput(
            skin = binding.dropdownSkin.text.toString(),
            season = binding.dropdownSeason.text.toString(),
            sex = binding.dropdownSex.text.toString(),
            situation = binding.dropdownSituation.text.toString(),
            style = binding.dropdownStyle.text.toString()
        )

        if (
            input.skin.isBlank() ||
            input.season.isBlank() ||
            input.sex.isBlank() ||
            input.situation.isBlank() ||
            input.style.isBlank()
        ) {
            Toast.makeText(requireContext(), "Vui lòng chọn đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                binding.progressLoading.visibility = View.VISIBLE
                binding.layoutResult.visibility = View.GONE

                val result = RetrofitClient.api.predict(input)

                val aoTrong = findClothes(result.ao_trong, result.mau_ao_trong)
                val aoKhoac = findClothes(result.ao_khoac, result.mau_ao_khoac)
                val quan = findClothes(result.quan, result.mau_quan)

                binding.tvResultInfo.text = """
                    ${result.ao_trong} ${result.mau_ao_trong}
                    ${result.ao_khoac} ${result.mau_ao_khoac}
                    ${result.quan} ${result.mau_quan}
                """.trimIndent()

                loadImage(aoTrong?.image, binding.imgAoTrong)
                loadImage(aoKhoac?.image, binding.imgAoKhoac)
                loadImage(quan?.image, binding.imgQuan)

                maleLink = aoTrong?.linkMale ?: aoKhoac?.linkMale ?: quan?.linkMale ?: ""
                femaleLink = aoTrong?.linkFemale ?: aoKhoac?.linkFemale ?: quan?.linkFemale ?: ""

                binding.layoutResult.visibility = View.VISIBLE

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressLoading.visibility = View.GONE
            }
        }
    }

    private suspend fun findClothes(type: String, color: String): Clothes? {
        val snapshot = db.collection("clothes")
            .whereEqualTo("type", type)
            .whereEqualTo("color", color)
            .limit(1)
            .get()
            .await()

        return snapshot.documents.firstOrNull()
            ?.toObject(Clothes::class.java)
    }

    private fun loadImage(url: String?, imageView: android.widget.ImageView) {
        Glide.with(requireContext())
            .load(url)
            .placeholder(R.drawable.fashion_banner)
            .error(R.drawable.fashion_banner)
            .into(imageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}