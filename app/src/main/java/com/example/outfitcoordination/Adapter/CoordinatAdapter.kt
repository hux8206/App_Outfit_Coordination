package com.example.outfitcoordination.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.outfitcoordination.Model.OutfitUIModel
import com.example.outfitcoordination.databinding.FragmentItemOutfitCardBinding

class CoordinatAdapter(private val list: MutableList<OutfitUIModel>): RecyclerView.Adapter<CoordinatAdapter.OutfitViewHolder>()  {

    inner class OutfitViewHolder(
        val binding : FragmentItemOutfitCardBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OutfitViewHolder {
        val binding = FragmentItemOutfitCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OutfitViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: OutfitViewHolder,
        position: Int
    ) {
        val item = list[position]
        Log.d("ADAPTER", "Bind position $position: ${list[position].aoTrongName}")
        Glide.with(holder.itemView.context)
            .load(item.aoTrongImage)
            .into(holder.binding.imgAoTrong)

        Glide.with(holder.itemView.context)
            .load(item.quanImage)
            .into(holder.binding.imgQuan)

        if(item.hasAoKhoac){
            holder.binding.imgAoKhoac.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(item.aoKhoacImage)
                .into(holder.binding.imgAoKhoac)
        }else{
            holder.binding.imgAoKhoac.visibility = View.GONE
        }
        holder.binding.tvCompatibility.text = "${item.compatibility.toInt()}%"
        holder.binding.tvInfo.text = "${item.aoTrongName} ${item.mauAoTrong} • " + "${item.aoKhoacName} ${item.mauAoKhoac} • " + "${item.quanName} ${item.mauQuan}"
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateData(newList: List<OutfitUIModel>){
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}