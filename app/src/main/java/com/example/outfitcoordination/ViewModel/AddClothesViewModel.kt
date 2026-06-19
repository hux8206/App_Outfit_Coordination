package com.example.outfitcoordination.ViewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.outfitcoordination.Repository.AddClothesRepository // Đảm bảo import đúng đường dẫn Repository của bạn

class AddClothesViewModel : ViewModel() {
    private val repository = AddClothesRepository()
    private val _isLoad = MutableLiveData<Boolean>()
    val isLoad: LiveData<Boolean> get() = _isLoad

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> get() = _isSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun addClothes(
        name: String, type: String, imageUri: Uri,
        color: String, maleLink: String, femaleLink: String
    ) {
        _isLoad.value = true
        viewModelScope.launch {
            val result = repository.addNewClothesByAdmin(
                imageUri, name, type, color, maleLink, femaleLink
            )

            if (result) {
                _isSuccess.value = true
            } else {
                _errorMessage.value = "Lỗi khi upload ảnh hoặc lưu dữ liệu!"
                _isSuccess.value = false
            }
            _isLoad.value = false
        }
    }
}