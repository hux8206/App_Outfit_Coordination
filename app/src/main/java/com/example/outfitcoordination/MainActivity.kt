package com.example.outfitcoordination.ViewModel // Đang khớp với vị trí hiện tại của bạn

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.outfitcoordination.R
import com.example.outfitcoordination.View.MyHome
import com.example.outfitcoordination.View.MyFashion
import com.example.outfitcoordination.View.MyFavorities
import com.example.outfitcoordination.View.MyProfile
// import com.example.outfitcoordination.View.MyWardrobe // Mở comment dòng này sau khi bạn tạo file MyWardrobe.kt nhé

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // File layout của bạn đang là activity_main.xml (nhớ đảm bảo nó chứa CoordinatorLayout như thiết kế)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val fabFavorite = findViewById<FloatingActionButton>(R.id.fabFavorite)

        // 1. Mặc định vừa vào app là mở ngay trang chính
        loadFragment(MyHome())

        // 2. Xử lý chuyển trang khi click các mục trên thanh Menu
        bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> MyHome()
                R.id.nav_fashion -> MyFashion()
                // R.id.nav_wardrobe -> MyWardrobe() // Mở comment sau khi bạn tạo file
                R.id.nav_profile -> MyProfile()
                else -> null
            }
            if (fragment != null) {
                loadFragment(fragment)
                true
            } else {
                false
            }
        }

        // 3. Xử lý khi click vào nút Trái tim nổi ở giữa
        fabFavorite.setOnClickListener {
            // Đánh dấu check vào mục placeholder để các nút khác tự bỏ sáng
            bottomNav.menu.findItem(R.id.nav_placeholder).isChecked = true
            loadFragment(MyFavorities())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}