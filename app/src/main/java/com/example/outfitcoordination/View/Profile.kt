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
import com.google.firebase.firestore.FirebaseFirestore

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

        val uid = auth.currentUser?.uid
        FirebaseFirestore.getInstance()
            .collection("outfits")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener {
                binding.numofoutfit.text = it.size().toString()
            }
        FirebaseFirestore.getInstance()
            .collection("clothes")
            .whereEqualTo("favourite",true)
            .get()
            .addOnSuccessListener {
                binding.numofclothes.text = it.size().toString()
            }
        FirebaseFirestore.getInstance()
            .collection("outfits")
            .whereEqualTo("userId", uid)
            .whereEqualTo("public",true)
            .get()
            .addOnSuccessListener {
                binding.numofpost.text = it.size().toString()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}