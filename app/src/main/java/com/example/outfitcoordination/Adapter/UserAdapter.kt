package com.example.outfitcoordination.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.outfitcoordination.Model.User
import com.example.outfitcoordination.R

class UserAdapter(
    private var userList: List<User>,
    private val onDeleteClick: (User) -> Unit,
    private val onLockClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvUserEmail: TextView = itemView.findViewById(R.id.tvUserEmail)
        val tvUserRole: TextView = itemView.findViewById(R.id.tvUserRole)
        val btnMoreOptions: ImageButton = itemView.findViewById(R.id.btnMoreOptions)
        val btnLockUser: ImageButton = itemView.findViewById(R.id.btnLockUser)
        val rootLayout: View = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_manage_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

        holder.tvUserName.text = user.name.ifEmpty { "Chưa cập nhật tên" }
        holder.tvUserEmail.text = user.email

        // Kiểm tra quyền Admin/User
        if (user.role == "admin") {
            holder.tvUserRole.text = "ADMIN"
            holder.tvUserRole.setTextColor(Color.parseColor("#D32F2F"))
            holder.btnMoreOptions.visibility = View.INVISIBLE
            holder.btnLockUser.visibility = View.INVISIBLE
        } else {
            holder.tvUserRole.text = "USER"
            holder.tvUserRole.setTextColor(Color.parseColor("#8B7355"))
            holder.btnMoreOptions.visibility = View.VISIBLE
            holder.btnLockUser.visibility = View.VISIBLE
        }

        // Kiểm tra trạng thái Khóa/Mở (0 là khóa, 1 là bình thường)
        if (user.state == 0) {
            holder.rootLayout.alpha = 0.5f
            holder.tvUserRole.text = "ĐÃ KHÓA"
            holder.tvUserRole.setTextColor(Color.parseColor("#999999"))
            holder.btnLockUser.setImageResource(android.R.drawable.ic_lock_idle_lock)
        } else {
            holder.rootLayout.alpha = 1.0f
            holder.btnLockUser.setImageResource(android.R.drawable.ic_lock_lock)
        }

        // Bắt sự kiện click
        holder.btnMoreOptions.setOnClickListener { onDeleteClick(user) }
        holder.btnLockUser.setOnClickListener { onLockClick(user) }
    }

    override fun getItemCount(): Int = userList.size

    fun updateList(newList: List<User>) {
        userList = newList
        notifyDataSetChanged()
    }
}