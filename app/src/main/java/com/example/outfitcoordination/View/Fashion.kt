package com.example.outfitcoordination.View

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.outfitcoordination.R
import com.example.outfitcoordination.databinding.FragmentFashionBinding
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Fashion : Fragment() {

    private var _binding: FragmentFashionBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private val outfitList = mutableListOf<GeneratedOutfit>()
    private lateinit var outfitAdapter: GeneratedOutfitAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFashionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        outfitAdapter = GeneratedOutfitAdapter(outfitList)

        binding.rvGeneratedOutfits.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvGeneratedOutfits.adapter = outfitAdapter
        binding.rvGeneratedOutfits.isNestedScrollingEnabled = false

        loadGeneratedOutfits()

        binding.chipGroup.setOnCheckedStateChangeListener { _, _ ->
            loadGeneratedOutfits()
        }
    }

    private fun loadGeneratedOutfits() {
        db.collection("outfits")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                outfitList.clear()

                for (doc in result) {
                    outfitList.add(doc.toObject(GeneratedOutfit::class.java))
                }

                outfitAdapter.notifyDataSetChanged()
                binding.tvEmpty.visibility =
                    if (outfitList.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener { e ->
                binding.tvEmpty.visibility = View.VISIBLE
                Toast.makeText(
                    requireContext(),
                    "Lỗi tải outfit: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class GeneratedOutfit(
        val aoTrongImage: String = "",
        val aoKhoacImage: String = "",
        val quanImage: String = "",
        val aoTrong: String = "",
        val aoKhoac: String = "",
        val quan: String = "",
        val createdAt: Long = 0L
    )

    inner class GeneratedOutfitAdapter(
        private val list: List<GeneratedOutfit>
    ) : RecyclerView.Adapter<GeneratedOutfitAdapter.OutfitViewHolder>() {

        inner class OutfitViewHolder(
            val card: MaterialCardView,
            val imgAoTrong: ImageView,
            val imgAoKhoac: ImageView,
            val imgQuan: ImageView,
            val tvTitle: TextView
        ) : RecyclerView.ViewHolder(card)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutfitViewHolder {
            val context = parent.context
            val density = context.resources.displayMetrics.density

            fun dp(value: Int): Int = (value * density).toInt()

            val card = MaterialCardView(context).apply {
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(230)
                ).apply {
                    setMargins(dp(4), dp(4), dp(4), dp(14))
                }
                radius = dp(18).toFloat()
                cardElevation = 0f
                setCardBackgroundColor(Color.WHITE)
                strokeWidth = dp(1)
                strokeColor = Color.parseColor("#EEE7DF")
            }

            val root = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            }

            val collage = FrameLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1f
                )
                setBackgroundColor(Color.parseColor("#FAF7F3"))
            }

            val imgAoTrong = ImageView(context).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                adjustViewBounds = true
                layoutParams = FrameLayout.LayoutParams(dp(82), dp(110)).apply {
                    gravity = Gravity.START or Gravity.CENTER_VERTICAL
                    leftMargin = dp(6)
                }
            }

            val imgQuan = ImageView(context).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                adjustViewBounds = true
                layoutParams = FrameLayout.LayoutParams(dp(70), dp(130)).apply {
                    gravity = Gravity.CENTER
                }
            }

            val imgAoKhoac = ImageView(context).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                adjustViewBounds = true
                layoutParams = FrameLayout.LayoutParams(dp(82), dp(110)).apply {
                    gravity = Gravity.END or Gravity.CENTER_VERTICAL
                    rightMargin = dp(6)
                }
            }

            val btnHeart = ImageButton(context).apply {
                setImageResource(R.drawable.ic_heart)
                setBackgroundResource(R.drawable.bg_circle_white_blur)
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                setPadding(dp(9), dp(9), dp(9), dp(9))
                layoutParams = FrameLayout.LayoutParams(dp(38), dp(38)).apply {
                    gravity = Gravity.TOP or Gravity.END
                    topMargin = dp(10)
                    rightMargin = dp(10)
                }
            }

            collage.addView(imgAoTrong)
            collage.addView(imgQuan)
            collage.addView(imgAoKhoac)
            collage.addView(btnHeart)

            val tvTitle = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(54)
                )
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(12), 0, dp(12), 0)
                setTextColor(Color.parseColor("#111111"))
                textSize = 13f
                maxLines = 2
            }

            root.addView(collage)
            root.addView(tvTitle)
            card.addView(root)

            return OutfitViewHolder(card, imgAoTrong, imgAoKhoac, imgQuan, tvTitle)
        }

        override fun onBindViewHolder(holder: OutfitViewHolder, position: Int) {
            val item = list[position]

            Glide.with(holder.itemView.context)
                .load(item.aoTrongImage)
                .placeholder(R.drawable.fashion_banner)
                .error(R.drawable.fashion_banner)
                .into(holder.imgAoTrong)

            Glide.with(holder.itemView.context)
                .load(item.aoKhoacImage)
                .placeholder(R.drawable.fashion_banner)
                .error(R.drawable.fashion_banner)
                .into(holder.imgAoKhoac)

            Glide.with(holder.itemView.context)
                .load(item.quanImage)
                .placeholder(R.drawable.fashion_banner)
                .error(R.drawable.fashion_banner)
                .into(holder.imgQuan)

            holder.tvTitle.text = "Outfit"
        }

        override fun getItemCount(): Int = list.size
    }
}