package com.example.outfitcoordination.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.outfitcoordination.Repository.AdminRepository

class AdminViewModel : ViewModel() {
    private val repository = AdminRepository()

    // Các LiveData để View quan sát
    val userCount = MutableLiveData<String>()
    val clothesCount = MutableLiveData<String>()
    val outfitCount = MutableLiveData<String>()

    fun loadStatistics() {
        repository.getStatistics { users, clothes, outfits ->
            userCount.value = users.toString()
            clothesCount.value = clothes.toString()
            outfitCount.value = outfits.toString()
        }
    }
}