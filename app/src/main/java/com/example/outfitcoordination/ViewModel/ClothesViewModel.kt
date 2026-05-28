package com.example.outfitcoordination.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.outfitcoordination.Model.Clothes
import com.example.outfitcoordination.Reponsitory.ClothesRepository

class ClothesViewModel : ViewModel() {
    private val repository = ClothesRepository()
    private val _clothes = MutableLiveData<List<Clothes>>()
    private val allClothesList = mutableListOf<Clothes>()
    val clothes : LiveData<List<Clothes>>get() = _clothes

    fun loadClothes(){
        repository.getClothes { list ->
            allClothesList.clear()
            allClothesList.addAll(list)

            _clothes.value = list
        }
    }

    fun filterClothes(category : String) {
        _clothes.value = when (category){
            "all" -> allClothesList

            "ao_trong" -> allClothesList.filter {
                it.type in listOf("ao_trong")
            }

            "ao_khoac" -> allClothesList.filter {
                it.type in listOf("ao_khoac")
            }

            "quan" -> allClothesList.filter {
                it.type in listOf("quan")
            }
            else -> allClothesList
        }
    }


}