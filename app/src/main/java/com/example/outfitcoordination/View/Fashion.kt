package com.example.outfitcoordination.View

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.outfitcoordination.Adapter.ClothesAdapter
import com.example.outfitcoordination.Adapter.CoordinatAdapter
import com.example.outfitcoordination.MainActivity
import com.example.outfitcoordination.Model.Clothes
import com.example.outfitcoordination.Model.OutfitUIModel
import com.example.outfitcoordination.R
import com.example.outfitcoordination.ViewModel.ClothesViewModel
import com.example.outfitcoordination.ViewModel.CoordinateViewModel
import com.example.outfitcoordination.databinding.FragmentFashionBinding
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Fashion : Fragment() {

    private var _binding: FragmentFashionBinding? = null
    private val binding get() = _binding!!
    private val viewmodel : CoordinateViewModel by viewModels()
    private val viewmodel2 : ClothesViewModel by activityViewModels()
    private lateinit var adapter : CoordinatAdapter
    private lateinit var adapter2 : ClothesAdapter
    private val outfitlist = mutableListOf<OutfitUIModel>()
    private val clothesList = mutableListOf<Clothes>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFashionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = CoordinatAdapter(
            list = outfitlist,
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
                bundle.putString("userId",outfit.userId )

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

        adapter2 = ClothesAdapter(
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
                bundle.putString("userId",clothes.userId)

                detail.arguments = bundle
                (requireActivity() as MainActivity).loadFragment(detail)
            },

            onCLickFavor = { clothes ->
                viewmodel2.toggleFavor(clothes) { isSuccess ->
                    if (isSuccess) {
                        clothes.favourite = !clothes.favourite
                        val message = if (clothes.favourite) "Đã thêm vào tủ đồ" else "Đã xóa khỏi tủ đồ"
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        adapter2.notifyDataSetChanged()
                    } else {
                        Toast.makeText(requireContext(), "Đã xảy ra lỗi !!", Toast.LENGTH_SHORT).show()
                    }
                }
            },

            onClickPublic = {clothes, isChecked ->
                viewmodel2.togglePublicClothes(clothes,isChecked)
            }
        )

        binding.rvFashion.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.rvFashion.setHasFixedSize(false)
        binding.rvFashion.adapter = adapter

        obserData()
        obserData2()
        viewmodel.getOutfitPublic()
        viewmodel2.getClothesPublic()

        binding.chipOutfit.setOnClickListener {
            binding.rvFashion.layoutManager = GridLayoutManager(requireContext(),1)
            binding.rvFashion.adapter = adapter
            viewmodel.getOutfitPublic()
        }
        binding.chipClothing.setOnClickListener {
            binding.rvFashion.layoutManager = GridLayoutManager(requireContext(),2)
            binding.rvFashion.adapter = adapter2
            viewmodel2.getClothesPublic()
        }

    }

    private fun obserData(){
        viewmodel.publicOutfit.observe(viewLifecycleOwner){list ->
            outfitlist.clear()
            outfitlist.addAll(list)
            adapter.notifyDataSetChanged()

            if (list.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvFashion.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvFashion.visibility = View.VISIBLE
            }
        }
    }
    private fun obserData2(){

        viewmodel2.publicClothes.observe(
            viewLifecycleOwner
        ){ list ->

            clothesList.clear()
            clothesList.addAll(list)

            adapter2.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}