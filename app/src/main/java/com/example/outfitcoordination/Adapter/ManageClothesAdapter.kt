package com.example.outfitcoordination.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.outfitcoordination.Model.Clothes
import com.example.outfitcoordination.databinding.ItemManageClothesBinding

class ManageClothesAdapter(
    private var list: List<Clothes>,
    private val onDeleteClick: (Clothes) -> Unit
) : RecyclerView.Adapter<ManageClothesAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemManageClothesBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemManageClothesBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.binding.tvClothesName.text = item.name.ifEmpty { "Chưa có tên" }
        holder.binding.tvClothesType.text = item.type

        // Glide sẽ tải link trực tiếp từ item.image (đường link Cloudinary của bạn)
        Glide.with(holder.itemView.context)
            .load(item.image)
            .into(holder.binding.imgClothes)

        holder.binding.btnDeleteClothes.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateList(newList: List<Clothes>) {
        list = newList
        notifyDataSetChanged()
    }
}