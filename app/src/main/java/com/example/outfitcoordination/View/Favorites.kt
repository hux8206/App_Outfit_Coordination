package com.example.outfitcoordination.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.outfitcoordination.Adapter.CoordinatAdapter
import com.example.outfitcoordination.MainActivity
import com.example.outfitcoordination.Model.OutfitUIModel
import com.example.outfitcoordination.R
import com.example.outfitcoordination.ViewModel.CoordinateViewModel
import com.example.outfitcoordination.databinding.FragmentFavoritesBinding

class Favorites : Fragment() {
    private val viewmodel : CoordinateViewModel by viewModels()
    private lateinit var adapter : CoordinatAdapter
    private var _binding : FragmentFavoritesBinding? = null
    private val binding get()= _binding!!
    private val outfitlist = mutableListOf<OutfitUIModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater,container,false)
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

                detail.arguments = bundle
                (requireActivity() as MainActivity).loadFragment(detail)
            }
        ){outfit ->
            viewmodel.removeFavorOutfit(outfit)
        }

        binding.rvFavorites.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.rvFavorites.setHasFixedSize(false)
        binding.rvFavorites.adapter = adapter

        obserData()
        viewmodel.getFavorOutfit()
    }

    private fun obserData(){
        viewmodel.favorOutfit.observe(viewLifecycleOwner){list ->
            outfitlist.clear()
            outfitlist.addAll(list)
            adapter.notifyDataSetChanged()

            if (list.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvFavorites.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvFavorites.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}