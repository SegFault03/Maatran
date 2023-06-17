package com.example.Maatran.services

data class UserModel(
    var name: String? = null,
    var gender: String? = null,
    var mobile: String? = null,
    var address: String? = null,
    var emergency: String? = null,
    var locality: String? = null,
    var email: String? = null,
    val age: Long = 0,
    val android_id: String? = null
)
