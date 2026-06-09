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
    val outfits : LiveData<List<OutfitUIModel>>get() = _outfits //truyen qua outfits de dua len UI

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
}