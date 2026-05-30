package com.example.outfitcoordination.View

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.outfitcoordination.ViewModel.ClothesViewModel
import com.example.outfitcoordination.databinding.FragmentDashboardBinding
import kotlinx.coroutines.flow.merge

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

                detail.arguments = bundle
                (requireActivity() as MainActivity).loadFragment(detail)
            }
        ) {clothes ->
            viewmodel.toggleFavor(clothes){isSuccess->
                if(isSuccess){
                    val message = if (clothes.favourite) "Da them vao tu do" else "da xoa khoi tu do"
                    Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()
                    clothesAdapter.notifyDataSetChanged()
                }else{
                    clothes.favourite = !clothes.favourite
                    Toast.makeText(requireContext(), "da xay ra loi !!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.rvOutfits.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvOutfits.adapter = clothesAdapter
        binding.rvOutfits.isNestedScrollingEnabled = false

        obserData()
        if (viewmodel.clothes.value == null){
            viewmodel.loadClothes()
        }

        binding.cardAll.setOnClickListener {
            viewmodel.filterClothes("all")
        }
        binding.cardAotrong.setOnClickListener {
            viewmodel.filterClothes("ao_trong")
        }
        binding.cardAokhoac.setOnClickListener {
            viewmodel.filterClothes("ao_khoac")
        }
        binding.cardQuan.setOnClickListener {
            viewmodel.filterClothes("quan")
        }

        binding.edtSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewmodel.search(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
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

// repository->viewmodel0->fragment->adapter->show UI