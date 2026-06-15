package com.example.outfitcoordination.View

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.outfitcoordination.Adapter.ClothesAdapter
import com.example.outfitcoordination.DisplayFormatter
import com.example.outfitcoordination.MainActivity
import com.example.outfitcoordination.Model.Clothes
import com.example.outfitcoordination.R
import com.example.outfitcoordination.ViewModel.ClothesViewModel
import com.example.outfitcoordination.databinding.FragmentClothesDetailBinding

class ClothesDetail : Fragment() {
    private var _binding : FragmentClothesDetailBinding? = null
    private val binding get() = _binding!!
    private val viewmodel : ClothesViewModel by activityViewModels()

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
        val id = arguments?.getString("id") ?: ""
        var favour = arguments?.getBoolean("favourite") ?: false

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

        if (favour){
            binding.btnfavordetail.setImageResource(R.drawable.ic_heart_fill)
        }else{
            binding.btnfavordetail.setImageResource(R.drawable.ic_heart)
        }

        binding.btnfavordetail.setOnClickListener {
            val currentclothes = Clothes(id = id, favourite = favour)
            viewmodel.toggleFavor(currentclothes){issuccess ->
                if (issuccess) {
                    favour = !favour
                    if (favour) {
                        binding.btnfavordetail.setImageResource(R.drawable.ic_heart_fill)
                        Toast.makeText(requireContext(),"da them vao tu do !!", Toast.LENGTH_SHORT).show()
                    }else{
                        binding.btnfavordetail.setImageResource(R.drawable.ic_heart)
                        Toast.makeText(requireContext(),"da xoa khoi tu do !", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(requireContext(),"loi !!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}