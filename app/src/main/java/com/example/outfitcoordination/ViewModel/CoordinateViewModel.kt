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

    fun toggleFavor(outfit: OutfitUIModel) {
        viewModelScope.launch {
            try {
                if (!outfit.favorite) {
                    val currentOutfitID = if (outfit.outfitID.isBlank()) {
                        repository.saveOutfit(outfit)
                    } else {
                        outfit.outfitID
                    }

                    // Lưu vào collection favorite_outfits
                    repository.toggleFavoriteOutfit(
                        outfit.copy(outfitID = currentOutfitID, favorite = false)
                    )
                    val mapFunc = { it: OutfitUIModel ->
                        if (it.outfitID == currentOutfitID || it == outfit) {
                            it.copy(favorite = true, outfitID = currentOutfitID)
                        } else {
                            it
                        }
                    }
                    _outfits.value = _outfits.value?.map(mapFunc)
                    _publicOutfit.value = _publicOutfit.value?.map(mapFunc)

                } else {
                    repository.toggleFavoriteOutfit(outfit)
                    val mapFunc = { it: OutfitUIModel ->
                        if (it.outfitID == outfit.outfitID) it.copy(favorite = false) else it
                    }
                    _outfits.value = _outfits.value?.map(mapFunc)
                    _publicOutfit.value = _publicOutfit.value?.map(mapFunc)
                    _favorOutfit.value = _favorOutfit.value?.filter { it.outfitID != outfit.outfitID }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun removeFavorOutfit(outfit : OutfitUIModel){
        viewModelScope.launch {
            repository.deleteOutfit(outfit.outfitID)
            val currentList = _outfits.value ?: emptyList()
            _outfits.value = currentList.filter {
                it.outfitID != outfit.outfitID
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
        if (outfit.outfitID.isBlank()) return

        val currentUid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        if (outfit.userId != currentUid) {
            _error.value = "Bạn không có quyền chỉnh sửa trạng thái Public của người khác!"
            return
        }
        viewModelScope.launch {
            repository.updataSwitchOutfit(outfit.outfitID, isPublic)
            val current = _outfits.value ?: return@launch
            _outfits.value = current.map {
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

    fun saveManualOutfit(
        aoTrong: com.example.outfitcoordination.Model.Clothes?,
        aoKhoac: com.example.outfitcoordination.Model.Clothes?,
        quan: com.example.outfitcoordination.Model.Clothes?,
        onResult: (Boolean, String) -> Unit
    ) {
        if (aoTrong == null || quan == null) {
            onResult(false, "Vui lòng chọn ít nhất 1 Áo trong và 1 Quần!")
            return
        }

        val newOutfit = OutfitUIModel(
            aoTrongImage = aoTrong.image,
            aoKhoacImage = aoKhoac?.image ?: "",
            quanImage = quan.image,

            aoTrongName = aoTrong.name,
            aoKhoacName = aoKhoac?.name ?: "khong_co",
            quanName = quan.name,

            mauAoTrong = aoTrong.color,
            mauAoKhoac = aoKhoac?.color ?: "khong_co",
            mauQuan = quan.color,

            hasAoKhoac = aoKhoac != null,
            compatibility = 100.0,

            aoTrongLink = aoTrong.female.ifBlank { aoTrong.male },
            aoKhoacLink = aoKhoac?.female?.ifBlank { aoKhoac.male } ?: "",
            quanLink = quan.female.ifBlank { quan.male },

            favorite = false
        )
        toggleFavor(newOutfit)
        onResult(true, "Đã lưu bộ phối vào mục Yêu thích!")
    }
}