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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.outfitcoordination.Adapter.CoordinatAdapter
import com.example.outfitcoordination.MainActivity
import com.example.outfitcoordination.Model.OutfitUIModel
import com.example.outfitcoordination.R
import com.example.outfitcoordination.ViewModel.CoordinateViewModel
import com.example.outfitcoordination.databinding.FragmentFashionBinding
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Fashion : Fragment() {

    private var _binding: FragmentFashionBinding? = null
    private val binding get() = _binding!!
    private val viewmodel : CoordinateViewModel by viewModels()
    private lateinit var adapter : CoordinatAdapter
    private val outfitlist = mutableListOf<OutfitUIModel>()

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

        binding.rvFashion.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.rvFashion.setHasFixedSize(false)
        binding.rvFashion.adapter = adapter

        obserData()
        viewmodel.getOutfitPublic()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}