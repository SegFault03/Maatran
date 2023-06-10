package com.example.Maatran.tests

import java.io.Serializable

data class UserData(var email: String? = null,
                    var name: String? = null,
                    var gender: String? = null,
                    var mobile: String? = null,
                    var address: String? = null,
                    var age: Long = 0,
                    var android_id: String? = null,
                    var locality: String? = null,
                    var emergency: String? = null
) : Serializable
