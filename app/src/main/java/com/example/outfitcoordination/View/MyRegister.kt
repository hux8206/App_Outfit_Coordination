package com.example.outfitcoordination.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.outfitcoordination.R

class MyRegister : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val layoutLogin = view.findViewById<LinearLayout>(R.id.layoutLogin)

        // Bấm quay lại hoặc "Đăng nhập" thì văng về màn hình Login
        val goBackToLogin = View.OnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnBack.setOnClickListener(goBackToLogin)
        layoutLogin.setOnClickListener(goBackToLogin)
    }
}