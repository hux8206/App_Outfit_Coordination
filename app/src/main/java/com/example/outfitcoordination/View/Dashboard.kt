package com.example.outfitcoordination.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.outfitcoordination.Adapter.ClothesAdapter
import com.example.outfitcoordination.MainActivity
import com.example.outfitcoordination.Model.Clothes
import com.example.outfitcoordination.ViewModel.ClothesViewModel
import com.example.outfitcoordination.databinding.FragmentDashboardBinding

class Dashboard : Fragment() {
    private val viewmodel : ClothesViewModel by viewModels()
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val clothesList = mutableListOf<Clothes>()
    private lateinit var clothesAdapter : ClothesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        clothesAdapter = ClothesAdapter(clothesList) {clothes ->
            val detail = ClothesDetail()
            val bundle = Bundle() //goi du lieu de chuyen tu man hinh nay sang man hinh khac
            bundle.putString("image",clothes.image)
            bundle.putString("female",clothes.female)
            bundle.putString("male",clothes.male)
            bundle.putString("name",clothes.name)
            bundle.putString("color",clothes.color)

            detail.arguments = bundle
            (requireActivity() as MainActivity).loadFragment(detail)
        }

        binding.rvOutfits.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvOutfits.adapter = clothesAdapter
        binding.rvOutfits.isNestedScrollingEnabled = false

        obserData()
        viewmodel.loadClothes()

        binding.cardAll.setOnClickListener {
            viewmodel.filterClothes("all")
        }
        binding.cardAotrong.setOnClickListener {
            viewmodel.filterClothes("ao_trong")
        }
        binding.cardAokhoac.setOnClickListener {
            viewmodel.filterClothes("ao_khoac")
        }
        binding.cardQuan.setOnClickListener {
            viewmodel.filterClothes("quan")
        }
    }

    private fun obserData(){
        viewmodel.clothes.observe(viewLifecycleOwner){ list ->
            clothesList.clear()
            clothesList.addAll(list)
            clothesAdapter.notifyDataSetChanged()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}