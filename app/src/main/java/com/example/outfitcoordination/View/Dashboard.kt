package com.example.outfitcoordination.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.outfitcoordination.Adapter.ClothesAdapter
import com.example.outfitcoordination.Model.Clothes
import com.example.outfitcoordination.Reponsitory.ClothesRepository
import com.example.outfitcoordination.ViewModel.ClothesViewModel
import com.example.outfitcoordination.databinding.FragmentDashboardBinding
import com.google.firebase.firestore.FirebaseFirestore

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
        clothesAdapter = ClothesAdapter(clothesList)

        binding.rvOutfits.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvOutfits.adapter = clothesAdapter
        binding.rvOutfits.isNestedScrollingEnabled = false

        obserData()
        viewmodel.loadClothes()
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