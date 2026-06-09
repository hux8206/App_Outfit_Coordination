package com.example.outfitcoordination.View

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.outfitcoordination.Adapter.CoordinatAdapter
import com.example.outfitcoordination.Model.ComboBoxInput
import com.example.outfitcoordination.ViewModel.CoordinateViewModel
import com.example.outfitcoordination.databinding.FragmentCoordinateBinding

class Coordinate : Fragment() {
    private val viewmodel : CoordinateViewModel by viewModels()
    private lateinit var adapter: CoordinatAdapter
    private var _binding : FragmentCoordinateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCoordinateBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDropdowns()
        setUpRecyclerView()
        observeData()

        binding.btnPredict.setOnClickListener {
            generateOutfit()
        }
    }

    private fun setUpRecyclerView(){
        adapter = CoordinatAdapter(mutableListOf())
        binding.rcvOutfits.layoutManager = GridLayoutManager(requireContext(),1)
        binding.rcvOutfits.setHasFixedSize(false)
        binding.rcvOutfits.adapter = adapter
    }

    private fun setupDropdowns() {
        binding.dropdownSkin.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                listOf("trang", "vang", "ngam"))
        )

        binding.dropdownSeason.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                listOf("nong", "mat", "lanh"))
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
                listOf("toi_gian", "streetwear", "lich_su", "sporty", "han_quoc", "vintage"))
        )
    }

    private fun observeData(){
        viewmodel.outfits.observe(viewLifecycleOwner){list ->
            adapter.updateData(list)
            binding.layoutResult.visibility = View.VISIBLE
            binding.tvEmpty.visibility = if(list.isEmpty()) View.VISIBLE else View.GONE
            Log.d("UI","${list.size}")
        }
        viewmodel.error.observe(viewLifecycleOwner){ error ->
            if (!error.isNullOrBlank()){
                Toast.makeText(requireContext(),error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateOutfit(){
        val text = binding.edtPrompt.text.toString().trim()
        if (text.isNotBlank()) {
            viewmodel.predictByText(text)
            return
        }

        val input = ComboBoxInput(
            skin = binding.dropdownSkin.text.toString(),
            season = binding.dropdownSeason.text.toString(),
            sex = binding.dropdownSex.text.toString(),
            situation = binding.dropdownSituation.text.toString(),
            style = binding.dropdownStyle.text.toString()
        )
        viewmodel.predictByComboBox(input)
    }
}