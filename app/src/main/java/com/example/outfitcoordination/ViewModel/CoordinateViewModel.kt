package com.example.outfitcoordination.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outfitcoordination.Model.ComboBoxInput
import com.example.outfitcoordination.Model.OutfitUIModel
import com.example.outfitcoordination.Reponsitory.CoordinateRepository
import kotlinx.coroutines.launch

class CoordinateViewModel: ViewModel() {
    private val repository = CoordinateRepository()
    private val _outfits = MutableLiveData<List<OutfitUIModel>>()
    private val _favorOutfit = MutableLiveData<List<OutfitUIModel>>()
    private val _publicOutfit = MutableLiveData<List<OutfitUIModel>>()
    val outfits : LiveData<List<OutfitUIModel>>get() = _outfits //truyen qua outfits de dua len UI
    val favorOutfit : LiveData<List<OutfitUIModel>>get() = _favorOutfit
    val publicOutfit : LiveData<List<OutfitUIModel>>get() = _publicOutfit
    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean>get() = _loading

    private val _error = MutableLiveData<String?>()
    val error : LiveData<String?>get() = _error

    fun predictByComboBox(input : ComboBoxInput){
        viewModelScope.launch {
            try{
                _loading.value = true
                _error.value = null

                val result = repository.predictByComboBox(input)
                _outfits.value = result //nhan ket qua tra ve
            }catch (e : Exception){
                _error.value = e.message
            }finally {
                _loading.value = false
            }
        }
    }

    fun predictByText(text: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val result = repository.predictByText(text)
                _outfits.value = result

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun toggleFavor(outfit : OutfitUIModel){
        viewModelScope.launch {
            try{
                val currentList = _outfits.value ?: return@launch

                if (!outfit.favorite){
                    val outfitID = repository.saveOutfit(outfit)

                    _outfits.value = currentList.map {
                        if (it == outfit){
                            it.copy(favorite = true, outfitID = outfitID)
                        }else{
                            it
                        }
                    }
                }else{
                    repository.deleteOutfit(outfit.outfitID)
                    _outfits.value = currentList.map {
                        if (it == outfit){
                            it.copy(favorite = false, outfitID = "")
                        }else{
                            it
                        }
                    }
                }
            }catch(e : Exception){
                _error.value = e.message
            }
        }
    }

    fun removeFavorOutfit(outfit : OutfitUIModel){
        viewModelScope.launch {
            repository.deleteOutfit(outfit.outfitID)
            val currentList = _outfits.value ?: emptyList()
            _outfits.value = currentList.filter {
                it.outfitID != outfit.outfitID // giu lai cac outfit khac outfit vua bam huy tim
            }
        }
    }

    fun getFavorOutfit(){
        viewModelScope.launch {
            _favorOutfit.value = repository.getFavorOutfit()
        }
    }

    fun togglePublicOutfit(
        outfit: OutfitUIModel,
        isPublic: Boolean
    ) {

        viewModelScope.launch {
            repository.updataSwitchOutfit(outfit.outfitID, isPublic)
            val current = _outfits.value ?: return@launch
            _outfits.value =
                current.map {
                    if(it.outfitID == outfit.outfitID){
                        it.copy(public = isPublic)
                    }else{
                        it
                    }
                }
        }
    }

    fun getOutfitPublic(){
        viewModelScope.launch {
            _publicOutfit.value = repository.getOutfitPublic()
        }
    }
}