package com.example.outfitcoordination.View

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.outfitcoordination.Adapter.ManageClothesAdapter
import com.example.outfitcoordination.MainActivity
import com.example.outfitcoordination.Model.Clothes
import com.example.outfitcoordination.R
import com.example.outfitcoordination.ViewModel.ManageClothesViewModel
import com.example.outfitcoordination.databinding.FragmentManageClothesBinding

class ManageClothes : Fragment() {
    private var _binding: FragmentManageClothesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ManageClothesViewModel by viewModels()
    private lateinit var manageAdapter: ManageClothesAdapter

    private var fullClothesList = listOf<Clothes>()
    private var currentFilterType = "all"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManageClothesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { (requireActivity() as MainActivity).loadFragment(Admin()) }

        binding.btnAddClothes.setOnClickListener {
            val addBottomSheet = AddClothesBottomSheet()
            addBottomSheet.onClothesAdded = { viewModel.loadClothes() } // Gọi viewModel tải lại
            addBottomSheet.show(parentFragmentManager, "AddClothesBottomSheet")
        }

        manageAdapter = ManageClothesAdapter(emptyList()) { confirmDelete(it) }
        binding.rvManageClothes.adapter = manageAdapter

        // Lắng nghe ViewModel
        viewModel.clothesList.observe(viewLifecycleOwner) { list ->
            fullClothesList = list
            binding.tvTotalClothes.text = "${list.size} món đồ trong hệ thống"
            applyFilters()
        }

        viewModel.message.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        // Ra lệnh tải dữ liệu
        viewModel.loadClothes()

        binding.chipGroupType.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                currentFilterType = when (checkedIds[0]) {
                    R.id.chipTop -> "ao_trong"
                    R.id.chipOuter -> "ao_khoac"
                    R.id.chipPants -> "quan"
                    else -> "all"
                }
                applyFilters()
            }
        }

        binding.edtSearchClothes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { applyFilters() }
        })
    }

    private fun applyFilters() {
        val search = binding.edtSearchClothes.text.toString().trim().lowercase()
        val filtered = fullClothesList.filter {
            val matchType = if (currentFilterType == "all") true else it.type == currentFilterType
            val matchSearch = it.name.lowercase().contains(search)
            matchType && matchSearch
        }
        manageAdapter.updateList(filtered)
    }

    private fun confirmDelete(clothes: Clothes) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa món đồ?")
            .setMessage("Xóa '${clothes.name}' vĩnh viễn?")
            .setPositiveButton("Xóa") { _, _ -> viewModel.deleteClothes(clothes) }
            .setNegativeButton("Hủy", null).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}