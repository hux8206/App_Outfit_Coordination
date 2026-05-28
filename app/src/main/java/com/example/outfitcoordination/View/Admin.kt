package com.example.outfitcoordination.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.outfitcoordination.MainActivity
import com.example.outfitcoordination.databinding.FragmentAdminBinding
import com.google.firebase.auth.FirebaseAuth

class Admin : Fragment() {
    private var _binding : FragmentAdminBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogoutadmin.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show()
            (requireActivity() as MainActivity).loadFragment(Dashboard())
        }
    }
}