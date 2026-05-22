package com.example.outfitcoordination.View

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import com.example.outfitcoordination.R
import com.example.outfitcoordination.ViewModel.UserViewModel
import com.example.outfitcoordination.databinding.FragmentLoginBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class Login : Fragment() {
    private var _binding : FragmentLoginBinding? = null
    private val viewmodel : UserViewModel by viewModels()
    private val binding get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        // 3. Khi click vào ô nhập email -> Trượt form lên
        binding.logemail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        // 4. Khi bấm nút X -> Trượt form xuống & ẩn bàn phím
        binding.btnClose.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            binding.logemail.clearFocus()

            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        // 5. Lắng nghe trạng thái trượt để làm mờ nút X và dòng Đăng ký
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.btnClose.visibility = View.VISIBLE
                    binding.layoutRegister.visibility = View.VISIBLE
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.btnClose.visibility = View.INVISIBLE
                    binding.layoutRegister.visibility = View.INVISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.btnClose.alpha = slideOffset
                binding.layoutRegister.alpha = slideOffset
            }
        })

        binding.login.setOnClickListener {
            val email = binding.logemail.text.toString().trim()
            val password = binding.logpassword.text.toString().trim()

            viewmodel.login(email,password)
        }

        viewmodel.SuccessLogin.observe(viewLifecycleOwner){success ->
            if(success){
                Toast.makeText(requireContext(),"dang nhap thanh cong", Toast.LENGTH_SHORT).show()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, Dashboard()).commit()
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