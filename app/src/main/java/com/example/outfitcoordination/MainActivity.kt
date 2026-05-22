package com.example.outfitcoordination // Đang khớp với vị trí hiện tại của bạn
import android.view.View
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.outfitcoordination.View.Dashboard
import com.example.outfitcoordination.View.Fashion
import com.example.outfitcoordination.View.Favorites
import com.example.outfitcoordination.View.Login
import com.example.outfitcoordination.View.Profile
import com.example.outfitcoordination.View.Register
import com.example.outfitcoordination.View.Wardrobe
import com.example.outfitcoordination.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // File layout của bạn đang là activity_main.xml (nhớ đảm bảo nó chứa CoordinatorLayout như thiết kế)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 1. Mặc định vừa vào app là mở ngay trang chính
        loadFragment(Dashboard())

        // 2. Xử lý chuyển trang khi click các mục trên thanh Menu
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> Dashboard()
                R.id.nav_fashion -> Fashion()
                R.id.nav_profile -> Profile()
                R.id.nav_wardrobe -> Wardrobe()
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
        binding.fabFavorite.setOnClickListener {
            // Đánh dấu check vào mục placeholder để các nút khác tự bỏ sáng
            binding.bottomNavigation.menu.findItem(R.id.nav_placeholder).isChecked = true
            loadFragment(Favorites())
        }
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        val hideNav = fragment is Login || fragment is Register

        binding.bottomNavigation.visibility = if (hideNav) View.GONE else View.VISIBLE
        binding.fabFavorite.visibility = if (hideNav) View.GONE else View.VISIBLE
    }
}