package com.example.outfitcoordination.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.outfitcoordination.MainActivity
import com.example.outfitcoordination.databinding.FragmentGuestProfileBinding
import com.example.outfitcoordination.databinding.FragmentLoginBinding
import kotlin.getValue

class GuestProfile : Fragment() {
    private var _binding : FragmentGuestProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGuestProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnLoginNow.setOnClickListener {
            (requireActivity() as MainActivity).loadFragment(Login())
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}