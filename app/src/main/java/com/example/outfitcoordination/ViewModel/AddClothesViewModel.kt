package com.example.outfitcoordination.ViewModel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.outfitcoordination.Repository.AddClothesRepository

class AddClothesViewModel : ViewModel() {
    private val repository = AddClothesRepository()

    // Quản lý trạng thái vòng xoay Loading, trạng thái Thành công và thông báo Lỗi
    val isLoad = MutableLiveData<Boolean>()
    val isSuccess = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    fun addClothes(name: String, type: String, imageUri: Uri) {
        isLoad.value = true // Bật trạng thái loading

        repository.uploadImageAndAddClothes(name, type, imageUri,
            onSuccess = {
                isLoad.value = false
                isSuccess.value = true
            },
            onFailure = { error ->
                isLoad.value = false
                errorMessage.value = error
            }
        )
    }
}