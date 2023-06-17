package com.example.Maatran.services

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository {
    private var apiInterface:ApiInterface?=null

    init {
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)
    }

    fun fetchAllUsers(): LiveData<List<UserModel>> {
        val data = MutableLiveData<List<UserModel>>()

        apiInterface?.fetchAllUsers()?.enqueue(object : Callback<List<UserModel>> {

            override fun onFailure(call: Call<List<UserModel>>, t: Throwable) {
                //data.value = null
            }

            override fun onResponse(
                call: Call<List<UserModel>>,
                response: Response<List<UserModel>>
            ) {

                val res = response.body()
                if (response.code() == 200 &&  res!=null){
                    data.value = res!!
                }else{
                    //data.value = null
                }
            }
        })
        return data
    }

    fun fetchUser(email: String): LiveData<UserModel> {
        val data = MutableLiveData<UserModel>()

        apiInterface?.fetchUser(email)?.enqueue(object : Callback<UserModel> {

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                //data.value = null
            }

            override fun onResponse(
                call: Call<UserModel>,
                response: Response<UserModel>
            ) {

                val res = response.body()
                Log.v("Tag", response.toString())
                if (response.code() == 200 &&  res!=null){
                    data.value = res!!
                }else{
                    //data.value = null
                }
            }
        })
        return data
    }

    fun createUser(userModel: UserModel):LiveData<UserModel>{
        val data = MutableLiveData<UserModel>()

        apiInterface?.createUser(userModel)?.enqueue(object : Callback<UserModel>{
            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                //data.value = null
            }

            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                val res = response.body()
                if (response.code() == 201 && res!=null){
                    data.value = res!!
                }else{
                    //data.value = null
                }
            }
        })
        return data
    }

    fun deleteUser(id:String):LiveData<Boolean>{
        val data = MutableLiveData<Boolean>()

        apiInterface?.deleteUser(id)?.enqueue(object : Callback<String>{
            override fun onFailure(call: Call<String>, t: Throwable) {
                data.value = false
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                data.value = response.code() == 200
            }
        })

        return data

    }
}