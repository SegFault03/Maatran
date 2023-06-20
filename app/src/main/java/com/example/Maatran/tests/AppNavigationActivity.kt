package com.example.Maatran.tests

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.Maatran.R
import com.example.Maatran.services.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AppNavigationActivity : AppCompatActivity(R.layout.dashboard_redesigned) {
    private var fragmentManager:FragmentManager = supportFragmentManager;
    private lateinit var bottomNavView: BottomNavigationView
    private var user: UserData? = null;
    private val viewModel: DashboardViewModel  by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchUserDetails()
        val viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        if(user==null)
            Log.v("Tag", "user is null")
        viewModel.user = user
        bottomNavView = findViewById(R.id.bottom_navigation)
        if(savedInstanceState == null){
            fragmentManager.beginTransaction().add(R.id.fragmentContainer, DashboardFragment())
                .setReorderingAllowed(true)
                .commit()
        }
    }

    private fun fetchUserDetails()
    {
        var db: FirebaseFirestore = FirebaseFirestore.getInstance()
        var mUser = FirebaseAuth.getInstance().currentUser
        if (mUser != null) {
            db.collection("UserDetails")
                .document(mUser.email.toString())
                .get()
                .addOnSuccessListener { task ->
                    val ds: DocumentSnapshot = task
                    var u:User = ds.toObject(User::class.java)!!
                    user = UserData(u.email, u.name, u.gender, u.mobile, u.address, u.age, u.android_id, u.emergency)
                }
        }
    }

    override fun onStart() {
        super.onStart()
        bottomNavView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.menu_dashboard -> {
                    fragmentManager.beginTransaction().replace(R.id.fragmentContainer,DashboardFragment())
                        .setReorderingAllowed(true)
                        .commit()
                    true
                }
                R.id.menu_bluetooth -> {
                    fragmentManager.beginTransaction().replace(R.id.fragmentContainer,BluetoothFragment())
                        .setReorderingAllowed(true)
                        .commit()
                    true
                }
                R.id.menu_family -> {
                    var fragment = FamilyViewFragment()
                    fragmentManager.beginTransaction().replace(R.id.fragmentContainer,fragment)
                        .setReorderingAllowed(true)
                        .commit()
                    true
                }
                R.id.menu_profile -> {
                    var fragment = ProfileViewFragment()
                    fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment)
                        .setReorderingAllowed(true)
                        .commit()
                    true
                }
                else -> false
            }
        }


    }
}