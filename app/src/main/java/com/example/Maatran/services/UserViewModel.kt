package com.example.Maatran.services

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class UserViewModel(application: Application): AndroidViewModel(application) {
    var createUserLiveData: LiveData<UserModel>?=null
    private var userRepository:UserRepository?=null
    var userModelListLiveData : LiveData<List<UserModel>>?=null
    var deleteUserLiveData:LiveData<Boolean>?=null
    var userData: LiveData<UserModel>?=null

    init {
        userRepository = UserRepository()
        userModelListLiveData = MutableLiveData()
        createUserLiveData = MutableLiveData()
        deleteUserLiveData = MutableLiveData()
        userData = MutableLiveData()
    }


    fun fetchAllUsers(){
        userModelListLiveData = userRepository?.fetchAllUsers()
    }

    fun createUser(userModel: UserModel){
        createUserLiveData = userRepository?.createUser(userModel)
    }

    fun deleteUser(id:String){
        deleteUserLiveData = userRepository?.deleteUser(id)
    }

    fun fetchUser(email:String){
        userData = userRepository?.fetchUser(email)
    }
}