package com.example.outfitcoordination.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.outfitcoordination.Adapter.ClothesAdapter
import com.example.outfitcoordination.MainActivity
import com.example.outfitcoordination.Model.Clothes
import com.example.outfitcoordination.R
import com.example.outfitcoordination.ViewModel.ClothesViewModel
import com.example.outfitcoordination.databinding.FragmentDashboardBinding
import com.example.outfitcoordination.databinding.FragmentWardrobeBinding
import kotlin.getValue

class Wardrobe : Fragment() {
    private val viewmodel : ClothesViewModel by viewModels()
    private var _binding: FragmentWardrobeBinding? = null
    private val binding get() = _binding!!
    private lateinit var clothesAdapter: ClothesAdapter
    private val clothesList = mutableListOf<Clothes>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWardrobeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        clothesAdapter = ClothesAdapter(
            list = clothesList,
            onItemClick = {clothes ->
                val detail = ClothesDetail()
                val bundle = Bundle() //goi du lieu de chuyen tu man hinh nay sang man hinh khac
                bundle.putString("image",clothes.image)
                bundle.putString("female",clothes.female)
                bundle.putString("male",clothes.male)
                bundle.putString("name",clothes.name)
                bundle.putString("color",clothes.color)
                bundle.putBoolean("favourite",clothes.favourite)
                bundle.putString("id",clothes.id)
                bundle.putBoolean("public",clothes.public)
                bundle.putString("userId",clothes.userId    )

                detail.arguments = bundle
                (requireActivity() as MainActivity).loadFragment(detail)
            },

            onCLickFavor = { clothes ->
                viewmodel.toggleFavor(clothes) { isSuccess ->
                    if (isSuccess) {
                        clothes.favourite = !clothes.favourite
                        val message = if (clothes.favourite) "Đã thêm vào tủ đồ" else "Đã xóa khỏi tủ đồ"
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        clothesAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(requireContext(), "Đã xảy ra lỗi !!", Toast.LENGTH_SHORT).show()
                    }
                }
            },

            onClickPublic = {clothes, isChecked ->
                viewmodel.togglePublicClothes(clothes,isChecked)
            }
        )

        binding.rvWardrobe.layoutManager = GridLayoutManager(requireContext(),2)
        binding.rvWardrobe.adapter = clothesAdapter
        binding.rvWardrobe.isNestedScrollingEnabled = false

        obserData()
        viewmodel.loadFavorClothes()

        if (viewmodel.clothes.value == null){
            viewmodel.loadClothes()
        }

        binding.wrAll.setOnClickListener {
            viewmodel.filterClothesFavor("all")
        }
        binding.wrAotrong.setOnClickListener {
            viewmodel.filterClothesFavor("ao_trong")
        }
        binding.wrAokhoac.setOnClickListener {
            viewmodel.filterClothesFavor("ao_khoac")
        }
        binding.wrTrouser.setOnClickListener {
            viewmodel.filterClothesFavor("quan")
        }

    }

    private fun obserData(){
        viewmodel.wrClothes.observe(viewLifecycleOwner){list ->
            clothesList.clear()
            clothesList.addAll(list)
            clothesAdapter.notifyDataSetChanged()

            if (list.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvWardrobe.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvWardrobe.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}