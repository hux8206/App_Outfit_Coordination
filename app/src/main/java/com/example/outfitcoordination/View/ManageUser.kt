package com.example.outfitcoordination.View

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.outfitcoordination.Adapter.UserAdapter
import com.example.outfitcoordination.MainActivity
import com.example.outfitcoordination.Model.User
import com.example.outfitcoordination.R
import com.example.outfitcoordination.ViewModel.ManageUserViewModel
import com.example.outfitcoordination.databinding.FragmentManageUserBinding

class ManageUser : Fragment() {
    private var _binding: FragmentManageUserBinding? = null
    private val binding get() = _binding!!

    // Gọi ViewModel
    private val viewModel: ManageUserViewModel by viewModels()
    private lateinit var userAdapter: UserAdapter

    private var fullUserList = listOf<User>()
    private var currentFilterRole = "all"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManageUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { (requireActivity() as MainActivity).loadFragment(Admin()) }

        userAdapter = UserAdapter(emptyList(),
            onDeleteClick = { confirmDelete(it) },
            onLockClick = { confirmLock(it) }
        )
        binding.rvManageUsers.adapter = userAdapter

        // Lắng nghe dữ liệu từ ViewModel
        viewModel.userList.observe(viewLifecycleOwner) { list ->
            fullUserList = list
            binding.tvTotalUsers.text = "${list.size} tài khoản trong hệ thống"
            applyFilters()
        }

        viewModel.message.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        // Ra lệnh lấy data
        viewModel.loadUsers()

        // Lọc
        binding.chipGroupUserType.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                currentFilterRole = when (checkedIds[0]) {
                    R.id.chipAdmin -> "admin"
                    R.id.chipCustomer -> "user"
                    else -> "all"
                }
                applyFilters()
            }
        }

        binding.edtSearchUser.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { applyFilters() }
        })
    }

    private fun applyFilters() {
        val search = binding.edtSearchUser.text.toString().trim().lowercase()
        val filtered = fullUserList.filter {
            val matchRole = if (currentFilterRole == "all") true else it.role == currentFilterRole
            val matchSearch = it.name.lowercase().contains(search) || it.email.lowercase().contains(search)
            matchRole && matchSearch
        }
        userAdapter.updateList(filtered)
    }

    private fun confirmDelete(user: User) {
        if (user.role == "admin") {
            Toast.makeText(requireContext(), "Không thể xóa Admin!", Toast.LENGTH_SHORT).show()
            return
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa tài khoản?")
            .setMessage("Chắc chắn xóa '${user.email}'?")
            .setPositiveButton("Xóa") { _, _ -> viewModel.deleteUser(user) } // Đẩy việc xóa cho ViewModel
            .setNegativeButton("Hủy", null).show()
    }

    private fun confirmLock(user: User) {
        val title = if (user.state == 0) "Mở khóa?" else "Vô hiệu hóa?"
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setPositiveButton("Đồng ý") { _, _ -> viewModel.toggleUserState(user) } // Đẩy cho ViewModel
            .setNegativeButton("Hủy", null).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}