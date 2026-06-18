package com.example.outfitcoordination.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.outfitcoordination.Adapter.ClothesAdapter
import com.example.outfitcoordination.Model.Clothes
import com.example.outfitcoordination.Model.OutfitUIModel
import com.example.outfitcoordination.ViewModel.ClothesViewModel
import com.example.outfitcoordination.ViewModel.CoordinateViewModel
import com.example.outfitcoordination.databinding.FragmentManualCoordinateBinding

class ManualCoordinate : Fragment() {
    private var _binding: FragmentManualCoordinateBinding? = null
    private val binding get() = _binding!!
    private val clothesViewModel: ClothesViewModel by activityViewModels()
    private val coordinateViewModel: CoordinateViewModel by activityViewModels()

    private lateinit var clothesAdapter: ClothesAdapter
    private val clothesList = mutableListOf<Clothes>()
    private var selectedAoTrong: Clothes? = null
    private var selectedAoKhoac: Clothes? = null
    private var selectedQuan: Clothes? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManualCoordinateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clothesAdapter = ClothesAdapter(
            list = clothesList,
            onItemClick = { clothes ->
                addClothesToCanvas(clothes)
            },
            onCLickFavor = { /* */ },
            onClickPublic = { _, _ -> /* */ }
        )

        binding.rvClothesSelect.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvClothesSelect.isNestedScrollingEnabled = false
        binding.rvClothesSelect.adapter = clothesAdapter

        clothesViewModel.clothes.observe(viewLifecycleOwner) { list ->
            clothesList.clear()
            clothesList.addAll(list)
            clothesAdapter.notifyDataSetChanged()
        }

        if (clothesViewModel.clothes.value == null) {
            clothesViewModel.loadClothes()
        } else {
            clothesViewModel.filterClothes("all")
        }
        binding.chipAll.setOnClickListener { clothesViewModel.filterClothes("all") }
        binding.chipAoTrong.setOnClickListener { clothesViewModel.filterClothes("ao_trong") }
        binding.chipAoKhoac.setOnClickListener { clothesViewModel.filterClothes("ao_khoac") }
        binding.chipQuan.setOnClickListener { clothesViewModel.filterClothes("quan") }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.btnCreateOutfit.setOnClickListener {
            coordinateViewModel.saveManualOutfit(
                selectedAoTrong,
                selectedAoKhoac,
                selectedQuan
            ) { isSuccess, message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                if (isSuccess) {
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }
    private fun addClothesToCanvas(clothes: Clothes) {
        when (clothes.type) {
            "ao_trong" -> {
                selectedAoTrong = clothes
                Glide.with(this).load(clothes.image).into(binding.imgCanvasAoTrong)
            }
            "ao_khoac" -> {
                selectedAoKhoac = clothes
                Glide.with(this).load(clothes.image).into(binding.imgCanvasAoKhoac)
            }
            "quan" -> {
                selectedQuan = clothes
                Glide.with(this).load(clothes.image).into(binding.imgCanvasQuan)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}