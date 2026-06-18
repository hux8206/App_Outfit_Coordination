package com.example.outfitcoordination.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.outfitcoordination.Model.Clothes
import com.example.outfitcoordination.R
import com.example.outfitcoordination.databinding.ItemOutfitDashboardBinding

class ClothesAdapter(
    private val list: List<Clothes>,
    private val onItemClick : (Clothes)->Unit,
    private val onCLickFavor : (Clothes) -> Unit,
    private val onClickPublic : (Clothes, Boolean) -> Unit
) : RecyclerView.Adapter<ClothesAdapter.ClothesViewHolder>() {
    inner class ClothesViewHolder(val binding: ItemOutfitDashboardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothesViewHolder { //tao khung de do giao dien
        val binding = ItemOutfitDashboardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClothesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClothesViewHolder, position: Int) { //do anh len khung da tao
        val item = list[position]

        Glide.with(holder.itemView.context)
            .load(item.image)
            .into(holder.binding.imgOutfit)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        if(item.favourite){
            holder.binding.btnFavorite.setImageResource(R.drawable.ic_heart_fill)
        }else{
            holder.binding.btnFavorite.setImageResource(R.drawable.ic_heart)
        }
        holder.binding.btnFavorite.setOnClickListener {
            onCLickFavor(item)
        }

        holder.binding.switchPublicClothes.isChecked = item.public
        holder.binding.switchPublicClothes.setOnCheckedChangeListener { _, isChecked ->
            onClickPublic(item,isChecked)
        }
    }

    override fun getItemCount(): Int = list.size // tra ve so luong item co trong list
}