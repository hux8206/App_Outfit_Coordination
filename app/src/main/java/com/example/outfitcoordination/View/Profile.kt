package com.example.outfitcoordination.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.outfitcoordination.MainActivity
import com.example.outfitcoordination.R
import com.example.outfitcoordination.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class Profile : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = auth.currentUser

        if (user != null) {
            binding.profileName.text = user.displayName ?: "Người dùng"
            binding.profileEmail.text = user.email ?: "Chưa có email"
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()

            Toast.makeText(requireContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show()

            (requireActivity() as MainActivity).loadFragment(Dashboard())
        }

        binding.btnFavorites.setOnClickListener {
            (requireActivity() as MainActivity).loadFragment(Favorites())
        }

        binding.btnWardrobe.setOnClickListener {
            (requireActivity() as MainActivity).loadFragment(Wardrobe())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}