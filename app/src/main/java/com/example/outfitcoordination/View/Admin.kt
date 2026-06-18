package com.example.outfitcoordination.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.outfitcoordination.MainActivity
import com.example.outfitcoordination.ViewModel.AdminViewModel
import com.example.outfitcoordination.databinding.FragmentAdminBinding
import com.google.firebase.auth.FirebaseAuth

class Admin : Fragment() {
    private var _binding : FragmentAdminBinding? = null
    private val binding get() = _binding!!

    // Khởi tạo ViewModel
    private val viewModel: AdminViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Quan sát dữ liệu từ ViewModel và cập nhật lên giao diện
        viewModel.userCount.observe(viewLifecycleOwner) { binding.tvUserCount.text = it }
        viewModel.clothesCount.observe(viewLifecycleOwner) { binding.tvClothesCount.text = it }
        viewModel.outfitCount.observe(viewLifecycleOwner) { binding.tvOutfitCount.text = it }

        // Ra lệnh cho ViewModel đi lấy data
        viewModel.loadStatistics()

        // Các nút bấm chuyển trang
        binding.btnLogoutadmin.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show()
            (requireActivity() as MainActivity).loadFragment(Dashboard())
        }
        binding.cardManageUsers.setOnClickListener {
            (requireActivity() as MainActivity).loadFragment(ManageUser())
        }
        binding.cardManageClothes.setOnClickListener {
            (requireActivity() as MainActivity).loadFragment(ManageClothes())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}