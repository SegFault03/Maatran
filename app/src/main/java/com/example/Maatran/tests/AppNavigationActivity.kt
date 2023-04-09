package com.example.Maatran.tests

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.example.Maatran.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class AppNavigationActivity : AppCompatActivity(R.layout.dashboard_redesigned) {
    private var fragmentManager:FragmentManager = supportFragmentManager;
    private lateinit var bottomNavView: BottomNavigationView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bottomNavView = findViewById(R.id.bottom_navigation)
        if(savedInstanceState == null){
            fragmentManager.beginTransaction().add(R.id.fragmentContainer, DashboardFragment())
                .setReorderingAllowed(true)
                .commit()
        }
    }

    override fun onStart() {
        super.onStart();
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
                    fragmentManager.beginTransaction().replace(R.id.fragmentContainer,FamilyViewFragment())
                        .setReorderingAllowed(true)
                        .commit()
                    true
                }
                R.id.menu_profile -> {
                    fragmentManager.beginTransaction().replace(R.id.fragmentContainer, ProfileViewFragment())
                        .setReorderingAllowed(true)
                        .commit()
                    true
                }
                else -> false
            }
        }


    }
}