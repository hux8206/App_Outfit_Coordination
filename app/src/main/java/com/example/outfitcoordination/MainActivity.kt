package com.example.outfitcoordination // Đang khớp với vị trí hiện tại của bạn
import android.content.Intent
import android.net.Uri
import android.view.View
import android.os.Bundle
import android.provider.Telephony
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.outfitcoordination.View.Admin
import com.example.outfitcoordination.View.Coordinate
import com.example.outfitcoordination.View.Dashboard
import com.example.outfitcoordination.View.Fashion
import com.example.outfitcoordination.View.Favorites
import com.example.outfitcoordination.View.GuestProfile
import com.example.outfitcoordination.View.Login
import com.example.outfitcoordination.View.ManageClothes
import com.example.outfitcoordination.View.ManageUser
import com.example.outfitcoordination.View.Profile
import com.example.outfitcoordination.View.Register
import com.example.outfitcoordination.View.Wardrobe
import com.example.outfitcoordination.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val config = mapOf(
            "cloud_name" to "dpfaidsiw",
            "api_key" to "719494448499853",
            "api_secret" to "tUnsWIrBiHeXbiD2xuczbOAwIHM"
        )
        com.cloudinary.android.MediaManager.init(this, config)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    checkRoleAndLoadHome()
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
                        loadFragment(Fashion())
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
                loadFragment(Coordinate())
            } else {
                Toast.makeText(this, "Bạn cần đăng nhập", Toast.LENGTH_SHORT).show()
                loadFragment(Login())
            }
        }
    }
    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
        val hideNav = fragment is Login || fragment is Register || fragment is Admin || fragment is ManageUser || fragment is ManageClothes
        binding.bottomAppBar.visibility = if (hideNav) View.GONE else View.VISIBLE
        binding.fabFavorite.visibility = if (hideNav) View.GONE else View.VISIBLE
    }

    private fun isLoggedIn(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        Toast.makeText(this, user?.email ?: "Chưa đăng nhập", Toast.LENGTH_SHORT).show()
        return user != null
    }

    fun openLink(link : String ){
        if (link.isNotBlank()){
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(intent)
        }else{
            Toast.makeText(this,"link not exist!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkRoleAndLoadHome() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val role = document.getString("role")
                        if (role == "admin") {
                            loadFragment(Admin())
                        } else {
                            loadFragment(Dashboard())
                        }
                    } else {
                        loadFragment(Dashboard())
                    }
                }
                .addOnFailureListener {
                    loadFragment(Dashboard())
                }
        } else {
            loadFragment(Dashboard())
        }
    }
}