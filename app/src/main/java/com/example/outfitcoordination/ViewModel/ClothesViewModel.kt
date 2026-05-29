package com.example.outfitcoordination.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outfitcoordination.Model.Clothes
import com.example.outfitcoordination.Reponsitory.ClothesRepository
import kotlinx.coroutines.launch

class ClothesViewModel : ViewModel() {
    private val repository = ClothesRepository()
    private val _clothes = MutableLiveData<List<Clothes>>()
    private val allClothesList = mutableListOf<Clothes>()
    val clothes : LiveData<List<Clothes>>get() = _clothes
    private var currentlist = "all"
    fun loadClothes(){
        repository.getClothes { list ->
            allClothesList.clear()
            allClothesList.addAll(list)

            filterClothes(currentlist)
        }
    }

    fun filterClothes(category : String) {
        currentlist = category

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

    fun search(keyword : String){
        var query = keyword.trim().lowercase()
        query = query.replace(" ","_")
        query = query.khongDau()
        if (query.isBlank()){
            _clothes.value = allClothesList
            return
        }

        _clothes.value = allClothesList.filter{
            it.name.lowercase().contains(query) || it.color.lowercase().contains(query)
        }
    }

    fun String.khongDau(): String {
        return this.map { char ->
            when (char) {
                'à', 'á', 'ạ', 'ả', 'ã', 'â', 'ầ', 'ấ', 'ậ', 'ẩ', 'ẫ', 'ă', 'ằ', 'ắ', 'ặ', 'ẳ', 'ẵ' -> 'a'

                'è', 'é', 'ẹ', 'ẻ', 'ẽ', 'ê', 'ề', 'ế', 'ệ', 'ể', 'ễ' -> 'e'

                'ì', 'í', 'ị', 'ỉ', 'ĩ' -> 'i'

                'ò', 'ó', 'ọ', 'ỏ', 'õ', 'ô', 'ồ', 'ố', 'ộ', 'ổ', 'ỗ', 'ơ', 'ờ', 'ớ', 'ợ', 'ở', 'ỡ' -> 'o'

                'ù', 'ú', 'ụ', 'ủ', 'ũ', 'ư', 'ừ', 'ứ', 'ự', 'ử', 'ữ' -> 'u'

                'ỳ', 'ý', 'ỵ', 'ỷ', 'ỹ' -> 'y'

                'đ'-> 'd'
                else -> char
            }
        }.joinToString("")
    }

    fun toggleFavor(clothes: Clothes, onComplete : (Boolean) -> Unit){
        clothes.favourite = !clothes.favourite
        viewModelScope.launch {
            repository.updateFavor(
                clothes.id,
                clothes.favourite,
                onComplete
            )
        }
    }
}