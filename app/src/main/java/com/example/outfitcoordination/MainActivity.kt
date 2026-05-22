package com.example.outfitcoordination // Đang khớp với vị trí hiện tại của bạn
import android.view.View
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.outfitcoordination.View.Dashboard
import com.example.outfitcoordination.View.Fashion
import com.example.outfitcoordination.View.Favorites
import com.example.outfitcoordination.View.GuestProfile
import com.example.outfitcoordination.View.Login
import com.example.outfitcoordination.View.Profile
import com.example.outfitcoordination.View.Register
import com.example.outfitcoordination.View.Wardrobe
import com.example.outfitcoordination.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    loadFragment(Dashboard())
                    true
                }

                R.id.nav_profile -> {
                    if (isLoggedIn()) {
                        loadFragment(Profile())
                    } else {
                        loadFragment(GuestProfile())
                    }
                    true
                }

                R.id.nav_wardrobe -> {
                    if (isLoggedIn()) {
                        loadFragment(Wardrobe())
                    } else {
                        Toast.makeText(this, "Bạn cần đăng nhập", Toast.LENGTH_SHORT).show()
                    }
                    true
                }

                R.id.nav_fashion -> {
                    if (isLoggedIn()) {
                        loadFragment(Favorites())
                    } else {
                        Toast.makeText(this, "Bạn cần đăng nhập", Toast.LENGTH_SHORT).show()
                    }
                    true
                }

                else -> false
            }
        }

        binding.fabFavorite.setOnClickListener {
            if (isLoggedIn()) {
                binding.bottomNavigation.menu.findItem(R.id.nav_placeholder).isChecked = true
                loadFragment(Favorites())
            } else {
                Toast.makeText(this, "Bạn cần đăng nhập", Toast.LENGTH_SHORT).show()
                loadFragment(Login())
            }
        }
    }
    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        val hideNav = fragment is Login || fragment is Register
        binding.bottomAppBar.visibility = if (hideNav) View.GONE else View.VISIBLE
        binding.fabFavorite.visibility = if (hideNav) View.GONE else View.VISIBLE
    }

    private fun isLoggedIn(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        Toast.makeText(this, user?.email ?: "Chưa đăng nhập", Toast.LENGTH_SHORT).show()
        return user != null
    }
}