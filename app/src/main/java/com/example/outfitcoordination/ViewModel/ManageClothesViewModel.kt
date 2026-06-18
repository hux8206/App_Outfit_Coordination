package com.example.outfitcoordination.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.outfitcoordination.Model.Clothes
import com.example.outfitcoordination.Repository.ManageClothesRepository

class ManageClothesViewModel : ViewModel() {
    private val repository = ManageClothesRepository()

    val clothesList = MutableLiveData<List<Clothes>>()
    val message = MutableLiveData<String>()

    fun loadClothes() {
        repository.getAllClothes(
            onSuccess = { clothesList.value = it },
            onFailure = { message.value = it }
        )
    }

    fun deleteClothes(clothes: Clothes) {
        repository.deleteClothes(clothes.id,
            onSuccess = {
                message.value = "Đã xóa thành công!"
                loadClothes()
            },
            onFailure = { message.value = "Lỗi khi xóa!" }
        )
    }
}