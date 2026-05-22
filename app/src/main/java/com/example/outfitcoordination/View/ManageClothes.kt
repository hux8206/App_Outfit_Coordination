package com.example.outfitcoordination.View

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.outfitcoordination.R
import com.example.outfitcoordination.databinding.FragmentManageClothesBinding

class ManageClothes : Fragment() {

    private var _binding: FragmentManageClothesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageClothesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnAddClothes.setOnClickListener {
            // TODO: mở picker chọn ảnh rồi upload Cloudinary/Firebase Storage
            // Sau khi có imageUrl thì lưu document vào Firestore collection "clothes"
        }

        // TODO:
        // 1. Tạo ManageClothesAdapter
        // 2. Query Firestore collection "clothes"
        // 3. Đổ dữ liệu vào binding.rvManageClothes
        // 4. Nút xóa trong adapter gọi confirmDelete()
    }

    private fun confirmDelete(onConfirm: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa món đồ?")
            .setMessage("Món đồ này sẽ bị xóa khỏi hệ thống.")
            .setNegativeButton("Hủy", null)
            .setPositiveButton("Xóa") { _, _ -> onConfirm() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}