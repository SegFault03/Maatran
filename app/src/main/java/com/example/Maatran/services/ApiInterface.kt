package com.example.Maatran.services

import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {
    @GET("UserDetails")
    fun fetchAllUsers(): Call<List<UserModel>>

    @GET("UserDetails/{email}")
    fun fetchUser(@Path("email") email:String):Call<UserModel>

    @POST("UserDetails/{userModel.email}")
    fun createUser(@Body userModel: UserModel):Call<UserModel>

    @DELETE("users/{id}")
    fun deleteUser(@Path("id") id:String):Call<String>
}