package com.example.outfitcoordination.View

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.outfitcoordination.MainActivity
import com.example.outfitcoordination.Model.ComboBoxInput
import com.example.outfitcoordination.Model.OutfitUIModel
import com.example.outfitcoordination.ViewModel.CoordinateViewModel
import com.example.outfitcoordination.databinding.FragmentCoordinateBinding
import com.google.firestore.bundle.BundleElement

class Coordinate : Fragment() {
    private val viewmodel : CoordinateViewModel by viewModels()
    private lateinit var adapter: CoordinatAdapter
    private var _binding : FragmentCoordinateBinding? = null
    private val binding get() = _binding!!
    val skinMap = mapOf(
        "Trắng" to "trang",
        "Vàng" to "vang",
        "Ngăm" to "ngam"
    )
    val seasonMap = mapOf(
        "Nóng" to "nong",
        "Mát" to "mat",
        "Lạnh" to "lanh"
    )
    val sexMap = mapOf(
        "Nam" to "nam",
        "Nữ" to "nu"
    )
    val situationMap = mapOf(
        "Đi học" to "di_hoc",
        "Đi làm" to "di_lam",
        "Đi chơi" to "di_choi"
    )
    val styleMap = mapOf(
        "Tối giản" to "toi_gian",
        "Lịch sự" to "lich_su",
        "Streetwear" to "streetwear",
        "Sporty" to "sporty",
        "Vintage" to "vintage"
    )
    private val outfitList = mutableListOf<OutfitUIModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCoordinateBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = CoordinatAdapter(
            list = outfitList,
            onClickDetail = { outfit ->
                val detail = OutfitDetail()
                val bundle = Bundle()
                bundle.putString("aoTrongImage",outfit.aoTrongImage)
                bundle.putString("aoKhoacImage",outfit.aoKhoacImage)
                bundle.putString("quanImage",outfit.quanImage)

                bundle.putString("aoTrongLink",outfit.aoTrongLink)
                bundle.putString("aoKhoacLink",outfit.aoKhoacLink)
                bundle.putString("quanLink",outfit.quanLink)

                bundle.putString("aoTrongName",outfit.aoTrongName)
                bundle.putString("aoKhoacName",outfit.aoKhoacName)
                bundle.putString("quanName",outfit.quanName)

                bundle.putDouble("compatibility",outfit.compatibility)
                bundle.putBoolean("favorite",outfit.favorite)
                bundle.putString("outfitID",outfit.outfitID)
                bundle.putBoolean("public",outfit.public)

                detail.arguments = bundle
                (requireActivity() as MainActivity).loadFragment(detail)
            },

            onClickFavor = {outfit ->
                viewmodel.toggleFavor(outfit)
            },

            onClickPublic = {outfit, isChecked ->
                viewmodel.togglePublicOutfit(outfit,isChecked)
            }
        )

        binding.rcvOutfits.layoutManager = GridLayoutManager(requireContext(),1)
        binding.rcvOutfits.setHasFixedSize(false)
        binding.rcvOutfits.adapter = adapter

        setupDropdowns()
        observeData()

        binding.btnPredict.setOnClickListener {

            binding.btnPredict.isEnabled = false

            generateOutfit()

            Handler(
                Looper.getMainLooper()
            ).postDelayed({

                binding.btnPredict.isEnabled = true

            }, 3000)
        }

        binding.btnFilter.setOnClickListener {
            (requireActivity() as MainActivity).loadFragment(ManualCoordinate())
        }
    }
    private fun setupDropdowns() {
        binding.dropdownSkin.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                skinMap.keys.toList())
        )

        binding.dropdownSeason.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                seasonMap.keys.toList())
        )

        binding.dropdownSex.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                sexMap.keys.toList())
        )

        binding.dropdownSituation.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                situationMap.keys.toList())
        )

        binding.dropdownStyle.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,
                styleMap.keys.toList())
        )
    }

    private fun observeData(){
        viewmodel.outfits.observe(viewLifecycleOwner){list ->
            adapter.updateData(list)
            binding.layoutResult.visibility = View.VISIBLE
            binding.tvEmpty.visibility = if(list.isEmpty()) View.VISIBLE else View.GONE
        }
        viewmodel.error.observe(viewLifecycleOwner){ error ->
            if (!error.isNullOrBlank()){
                Toast.makeText(requireContext(),error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateOutfit(){
        val text = binding.edtPrompt.text.toString().trim()
        Log.d("PREDICT", "TEXT = $text")
        if (text.isNotBlank()) {
            viewmodel.predictByText(text)
            return
        }

        val input = ComboBoxInput(
            skin = skinMap[binding.dropdownSkin.text.toString()] ?: "",
            season = seasonMap[binding.dropdownSeason.text.toString()] ?: "",
            sex = sexMap[binding.dropdownSex.text.toString()] ?: "",
            situation = situationMap[binding.dropdownSituation.text.toString()] ?: "",
            style = styleMap[binding.dropdownStyle.text.toString()] ?: ""
        )
        viewmodel.predictByComboBox(input)
    }
}