package com.example.outfitcoordination.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.outfitcoordination.Model.Clothes
import com.example.outfitcoordination.Reponsitory.ClothesRepository

class ClothesViewModel : ViewModel() {
    private val repository = ClothesRepository()
    private val _clothes = MutableLiveData<List<Clothes>>()
    val clothes : LiveData<List<Clothes>>get() = _clothes

    fun loadClothes(){
        repository.getClothes { list ->
            _clothes.value = list
        }
    }
}