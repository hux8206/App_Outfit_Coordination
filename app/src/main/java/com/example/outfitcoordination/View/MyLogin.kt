package com.example.outfitcoordination.View

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.example.outfitcoordination.R

class MyLogin : Fragment() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Nạp giao diện từ file fragment_my_login.xml
        return inflater.inflate(R.layout.fragment_my_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Ánh xạ các view (Lưu ý ID phải khớp với file XML)
        val bottomSheet = view.findViewById<ConstraintLayout>(R.id.bottomSheet)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val btnClose = view.findViewById<ImageButton>(R.id.btnClose)
        val layoutRegister = view.findViewById<LinearLayout>(R.id.layoutRegister)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)

        // 2. Khởi tạo Behavior cho form trượt
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        // 3. Khi click vào ô nhập email -> Trượt form lên
        etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        // 4. Khi bấm nút X -> Trượt form xuống & ẩn bàn phím
        btnClose.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            etEmail.clearFocus()

            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        // 5. Lắng nghe trạng thái trượt để làm mờ nút X và dòng Đăng ký
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    btnClose.visibility = View.VISIBLE
                    layoutRegister.visibility = View.VISIBLE
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    btnClose.visibility = View.INVISIBLE
                    layoutRegister.visibility = View.INVISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                btnClose.alpha = slideOffset
                layoutRegister.alpha = slideOffset
            }
        })
    }
}