package com.example.Maatran.tests

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class DashboardViewModel: ViewModel() {
    var user: UserData? = null
    var isPatient: Boolean = false
    val  db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun isPatient() {
        isPatient = true
    }
}