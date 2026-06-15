package com.example.outfitcoordination.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.outfitcoordination.DisplayFormatter
import com.example.outfitcoordination.Model.OutfitUIModel
import com.example.outfitcoordination.R
import com.example.outfitcoordination.View.OutfitDetail
import com.example.outfitcoordination.databinding.FragmentItemOutfitCardBinding

class CoordinatAdapter(
    private val list: MutableList<OutfitUIModel>,
    private val onClickDetail: (OutfitUIModel) -> Unit,
    private val onClickFavor : (OutfitUIModel) -> Unit
): RecyclerView.Adapter<CoordinatAdapter.OutfitViewHolder>()  {

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
        holder.binding.btnDetail.setOnClickListener {
            onClickDetail(item)
        }
        holder.binding.tvCompatibility.text = "${item.compatibility.toInt()}%"
        val aoTrongText = "${DisplayFormatter.clothesName(item.aoTrongName)} " + "${DisplayFormatter.clothesName(item.mauAoTrong)}"
        val quanText = "${DisplayFormatter.clothesName(item.quanName)} " + "${DisplayFormatter.clothesName(item.mauQuan)}"
        val aoKhoacText =
            if (item.aoKhoacName != "khong_co" && item.mauAoKhoac != "khong_co") {
                " • " + "${DisplayFormatter.clothesName(item.aoKhoacName)} " + "${DisplayFormatter.clothesName(item.mauAoKhoac)}"
            } else {
                ""
            }
        holder.binding.tvInfo.text = aoTrongText + aoKhoacText + " • " + quanText

        if(item.favorite){
            holder.binding.btnFavoriteCoordinate.setImageResource(
                R.drawable.ic_heart_fill
            )
        }else{
            holder.binding.btnFavoriteCoordinate.setImageResource(
                R.drawable.ic_heart
            )
        }

        holder.binding.btnFavoriteCoordinate.setOnClickListener {
            onClickFavor(item)
        }
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