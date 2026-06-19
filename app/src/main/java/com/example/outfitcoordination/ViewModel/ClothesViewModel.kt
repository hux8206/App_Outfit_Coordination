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
    private val _wrClothes = MutableLiveData<List<Clothes>>()
    private val _publicClothes = MutableLiveData<List<Clothes>>()
    private val allClothesList = mutableListOf<Clothes>()
    val clothes : LiveData<List<Clothes>>get() = _clothes
    val wrClothes : LiveData<List<Clothes>>get() = _wrClothes
    val publicClothes : LiveData<List<Clothes>>get() = _publicClothes
    private var currentlist = "all"
    private val _error = MutableLiveData<String?>()
    val error : LiveData<String?>get() = _error
    fun loadClothes() {
        repository.getClothesForCurrentUser { list ->
            allClothesList.clear()
            allClothesList.addAll(list)

            filterClothes(currentlist)
        }
    }

    fun getClothesPublic(){
        viewModelScope.launch {
            _publicClothes.value = repository.getClothesPublic()
        }
    }

    fun loadFavorClothes() {
        filterClothesFavor("all")
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

    fun filterClothesFavor(category : String) {
        currentlist = category

        val favorList = allClothesList.filter {
            it.favourite
        }

        _wrClothes.value = when (category){
            "all" -> favorList

            "ao_trong" -> favorList.filter {
                it.type in listOf("ao_trong")
            }

            "ao_khoac" -> favorList.filter {
                it.type in listOf("ao_khoac")
            }

            "quan" -> favorList.filter {
                it.type in listOf("quan")
            }
            else -> favorList
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

    fun toggleFavor(
        clothes: Clothes,
        onComplete: (Boolean) -> Unit
    ) {
        val newState = !clothes.favourite

        repository.updateFavor(clothes) { isSuccess ->
            if (isSuccess) {
                allClothesList.find { it.id == clothes.id }?.favourite = newState
                filterClothes(currentlist)
                _publicClothes.value = _publicClothes.value?.map {
                    if (it.id == clothes.id) it.copy(favourite = newState) else it
                }
            }
            onComplete(isSuccess)
        }
    }

    fun togglePublicClothes(
        clothes : Clothes,
        isPublic: Boolean
    ) {
        if (clothes.id.isBlank()) return

        viewModelScope.launch {
            try {
                repository.updataSwitchOutfit(clothes.id, isPublic)
                allClothesList.find { it.id == clothes.id }?.public = isPublic
                val current = _clothes.value ?: return@launch
                _clothes.value = current.map {
                    if(it.id == clothes.id){
                        it.copy(public = isPublic)
                    }else{
                        it
                    }
                }

                if (isPublic) {
                    getClothesPublic()
                } else {
                    _publicClothes.value = _publicClothes.value?.filter { it.id != clothes.id }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}