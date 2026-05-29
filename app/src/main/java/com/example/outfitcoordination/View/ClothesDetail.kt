package com.example.outfitcoordination.View

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.outfitcoordination.DisplayFormatter
import com.example.outfitcoordination.MainActivity
import com.example.outfitcoordination.databinding.FragmentClothesDetailBinding

class ClothesDetail : Fragment() {
    private var _binding : FragmentClothesDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClothesDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val image = arguments?.getString("image") ?: ""
        val name = arguments?.getString("name") ?: ""
        val color = arguments?.getString("color") ?: ""
        val linkmale = arguments?.getString("male") ?: ""
        val linkfemale = arguments?.getString("female") ?: ""

        binding.txtname.text = DisplayFormatter.clothesName(name)
        val drawable = binding.viewColor.background.mutate() as GradientDrawable
        drawable.setColor(DisplayFormatter.getColor(color))

        Glide.with(requireContext()).load(image).into(binding.imgDetail)

        binding.btnBuyFemale.setOnClickListener {
            (requireActivity() as MainActivity).openLink(linkfemale)
        }
        binding.btnBuyMale.setOnClickListener {
            (requireActivity() as MainActivity).openLink(linkmale)
        }
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}