package com.example.outfitcoordination.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.outfitcoordination.DisplayFormatter
import com.example.outfitcoordination.MainActivity
import com.example.outfitcoordination.Model.OutfitUIModel
import com.example.outfitcoordination.R
import com.example.outfitcoordination.ViewModel.CoordinateViewModel
import com.example.outfitcoordination.databinding.FragmentOutfitDetailBinding
import com.google.android.play.integrity.internal.b

class OutfitDetail : Fragment() {
    private var _binding : FragmentOutfitDetailBinding? = null
    private val binding get() = _binding!!
    private val viewmodel : CoordinateViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOutfitDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val aoTrongImage = arguments?.getString("aoTrongImage") ?: ""
        val aoKhoacImage = arguments?.getString("aoKhoacImage") ?: ""
        val quanImage = arguments?.getString("quanImage") ?: ""

        val aoTrongLink = arguments?.getString("aoTrongLink") ?: ""
        val aoKhoacLink = arguments?.getString("aoKhoacLink") ?: ""
        val quanLink = arguments?.getString("quanLink") ?: ""

        val aoTrongName = arguments?.getString("aoTrongName") ?: ""
        val aoKhoacName = arguments?.getString("aoKhoacName") ?: ""
        val quanName = arguments?.getString("quanName") ?: ""

        val compatibility = arguments?.getDouble("compatibility") ?: ""
        var favorite = arguments?.getBoolean("favorite") ?: false
        val outfitID = arguments?.getString("outfitID") ?: ""
        var publicinfo = arguments?.getBoolean("public") ?: false

        Glide.with(requireContext()).load(aoTrongImage).into(binding.imgAoTrong)
        Glide.with(requireContext()).load(aoKhoacImage).into(binding.imgAoKhoac)
        Glide.with(requireContext()).load(quanImage).into(binding.imgQuan)

        binding.tvScore.text = "Độ phù hợp : ${compatibility} %"

        if (favorite){
            binding.btnFavoriteDetailOutfit.setImageResource(R.drawable.ic_heart_fill)
        }else{
            binding.btnFavoriteDetailOutfit.setImageResource(R.drawable.ic_heart)
        }

        binding.btnFavoriteDetailOutfit.setOnClickListener {
            val currentOutfit = OutfitUIModel(favorite = favorite, outfitID = outfitID)
            if (favorite){
                viewmodel.removeFavorOutfit(currentOutfit)
            }else{
                viewmodel.toggleFavor(currentOutfit)
            }
        }

        binding.tvAoTrong.text = DisplayFormatter.clothesName(aoTrongName)
        binding.tvAoKhoac.text = DisplayFormatter.clothesName(aoKhoacName)
        binding.tvQuan.text = DisplayFormatter.clothesName(quanName)

        binding.btnBuyAoTrong.setOnClickListener {
            (requireActivity() as MainActivity).openLink(aoTrongLink)
        }
        binding.btnBuyAoKhoac.setOnClickListener {
            (requireActivity() as MainActivity).openLink(aoKhoacLink)
        }
        binding.btnBuyQuan.setOnClickListener {
            (requireActivity() as MainActivity).openLink(quanLink)
        }

        binding.btnBackInOutfitDetail.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val creatorUid = arguments?.getString("userId") ?: ""
        val currentUid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        binding.switchPublic.isChecked = publicinfo

        if (outfitID.isNotBlank() && creatorUid != currentUid) {
            binding.switchPublic.isEnabled = false
        } else {
            binding.switchPublic.isEnabled = true
        }

        binding.switchPublic.setOnCheckedChangeListener { _, isChecked ->
            val currentOutfit = OutfitUIModel(outfitID = outfitID, public = publicinfo)
            publicinfo = isChecked
            viewmodel.togglePublicOutfit(currentOutfit,publicinfo)
            Toast.makeText(requireContext(),
                if(isChecked) "Đã được public" else "Đã hủy public",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}