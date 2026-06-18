package com.example.outfitcoordination.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.outfitcoordination.Model.User
import com.example.outfitcoordination.Repository.ManageUserRepository

class ManageUserViewModel : ViewModel() {
    private val repository = ManageUserRepository()

    val userList = MutableLiveData<List<User>>()
    val message = MutableLiveData<String>()

    fun loadUsers() {
        repository.getAllUsers(
            onSuccess = { userList.value = it },
            onFailure = { message.value = it }
        )
    }

    fun deleteUser(user: User) {
        repository.deleteUser(user.id,
            onSuccess = {
                message.value = "Xóa thành công!"
                loadUsers() // Tải lại sau khi xóa
            },
            onFailure = { message.value = "Lỗi khi xóa!" }
        )
    }

    fun toggleUserState(user: User) {
        val newState = if (user.state == 0) 1 else 0
        repository.updateUserState(user.id, newState,
            onSuccess = {
                message.value = "Cập nhật thành công!"
                loadUsers()
            },
            onFailure = { message.value = "Lỗi khi cập nhật!" }
        )
    }
}