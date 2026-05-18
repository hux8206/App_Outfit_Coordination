package com.example.outfitcoordination.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.outfitcoordination.R
import com.example.outfitcoordination.ViewModel.UserViewModel
import com.example.outfitcoordination.databinding.FragmentLoginBinding

class Login : Fragment() {
    private var _binding : FragmentLoginBinding? = null
    private val viewmodel : UserViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.login.setOnClickListener {
            val email = binding.logemail.text.toString().trim()
            val password = binding.logpassword.text.toString().trim()

            viewmodel.login(email,password)
        }

        viewmodel.SuccessLogin.observe(viewLifecycleOwner){success ->
            if(success){
                Toast.makeText(requireContext(),"dang nhap thanh cong", Toast.LENGTH_SHORT).show()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main,Home()).commit()
            }else{
                Toast.makeText(requireContext(),"dang nhap that bai", Toast.LENGTH_SHORT).show()
            }
        }

        binding.logregis.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main, Register()).commit()
        }
    }
}