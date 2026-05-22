package com.example.outfitcoordination.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.outfitcoordination.Reponsitory.UserRepository
import androidx.lifecycle.viewModelScope
import com.example.outfitcoordination.Model.User
import kotlinx.coroutines.launch

class UserViewModel: ViewModel(){
    private val repository = UserRepository()
    private val _SuccessRegister = MutableLiveData<Boolean>()
    private val _SuccessLogin = MutableLiveData<Boolean>()
    val SuccessRegister : LiveData<Boolean> = _SuccessRegister //nhan gia tri tu _success va truyen qua lai UI de hien thi
    val SuccessLogin : LiveData<Boolean> = _SuccessLogin
    fun register(name : String, email : String, password: String){ //lay du lieu tu ui
        val user = User(          //tao user de truyen qua repository
            name = name,
            email = email,
            state = 1,
            role = "user"
        )

        viewModelScope.launch {
            _SuccessRegister.value = repository.register(user,password) //nhan tra gia true or false duoc authen tra ve, sau do truyen len success
        }
    }

    fun login(email: String, password: String){
        viewModelScope.launch {
            _SuccessLogin.value = repository.login(email,password)
        }
    }
}