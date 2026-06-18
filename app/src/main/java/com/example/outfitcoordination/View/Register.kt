package com.example.outfitcoordination.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.outfitcoordination.R
import com.example.outfitcoordination.ViewModel.UserViewModel
import com.example.outfitcoordination.databinding.FragmentRegisterBinding
import androidx.fragment.app.viewModels
class Register : Fragment() {
    private var _binding : FragmentRegisterBinding? = null //co the null vi luc tao hoac chuyen fragment co the destroy
    private val viewmodel : UserViewModel by viewModels()
    private val binding get() = _binding!! // !!khong null
    override fun onCreateView( //tao view file xml
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.regis.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            viewmodel.register(name, email, password)
        }

        viewmodel.SuccessRegister.observe(viewLifecycleOwner){ success ->
            if (success){
                Toast.makeText(requireContext(),"dang ki thanh cong", Toast.LENGTH_SHORT).show()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, Login()).commit()
            }else{
                viewmodel.error.observe(viewLifecycleOwner){ error ->
                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.regislog.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, Login()).commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}