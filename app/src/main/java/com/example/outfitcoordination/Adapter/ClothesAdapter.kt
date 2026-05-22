package com.example.outfitcoordination.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.R
import com.example.outfitcoordination.Model.Clothes
import com.example.outfitcoordination.databinding.ItemOutfitDashboardBinding

class ClothesAdapter(
    private val list: List<Clothes>
) : RecyclerView.Adapter<ClothesAdapter.ClothesViewHolder>() {

    inner class ClothesViewHolder(
        val binding: ItemOutfitDashboardBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothesViewHolder {
        val binding = ItemOutfitDashboardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClothesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClothesViewHolder, position: Int) {
        val item = list[position]

        Glide.with(holder.itemView.context)
            .load(item.image)
            .into(holder.binding.imgOutfit)
    }

    override fun getItemCount(): Int = list.size
}