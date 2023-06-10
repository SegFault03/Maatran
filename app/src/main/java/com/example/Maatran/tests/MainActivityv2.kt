package com.example.Maatran.tests

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.example.Maatran.R
import com.example.Maatran.databinding.ActivityMainBinding
import com.example.Maatran.ui.DashboardActivity
import com.example.Maatran.ui.EditPatientActivity
import com.example.Maatran.ui.MainActivity
import com.example.Maatran.ui.WelcomeActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Objects

class MainActivityv2 : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val TAG = "MainActivityv2"

    private var lastUpdatedDot: Int = 0

    private lateinit var loadingDotHandler: Handler

    private lateinit var signInHandler: Handler

    private var signInRunnable: Runnable = Runnable {
        Toast.makeText(this,"Its taking too long, please log-in/sign-up manually",Toast.LENGTH_SHORT).show()
        val intent = Intent(applicationContext, WelcomeActivity::class.java)
        startActivity(intent)
        super.finish()
    }
    private val loadingDotRunnable: Runnable = Runnable { changeLoadingDot() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)
        loadingDotHandler = Handler(Looper.getMainLooper())
        loadingDotHandler.postDelayed(loadingDotRunnable,500)
        signInHandler = Handler(Looper.getMainLooper())
        signInHandler.postDelayed(signInRunnable,8000)
    }

    private fun changeLoadingDot()
    {
        val currLoadDotView = "load_dot" + lastUpdatedDot % 3
        val oldLoadDotView = "load_dot" + if (lastUpdatedDot % 3 == 0) 2 else lastUpdatedDot % 3 - 1
        lastUpdatedDot++
        val currResID = resources.getIdentifier(currLoadDotView, "id", packageName)
        val oldResID = resources.getIdentifier(oldLoadDotView, "id", packageName)
        val currResView = findViewById<AppCompatImageView>(currResID)
        val oldResView = findViewById<AppCompatImageView>(oldResID)
        val drawable = AppCompatResources.getDrawable(this, R.drawable.loading_dot_grey)
        drawable!!.setColorFilter(
            ContextCompat.getColor(this, R.color.colorPrimary),
            PorterDuff.Mode.SRC_IN
        )
        currResView.setImageDrawable(drawable)
        oldResView.setImageResource(R.drawable.loading_dot_grey)
        loadingDotHandler.postDelayed(loadingDotRunnable, 500)
    }

    fun signInOptions() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            val intent = Intent(applicationContext, WelcomeActivity::class.java)
            startActivity(intent)
            super.finish()
        } else {
            checkForUserDetails(user)
        }
    }

    private fun checkForUserDetails(user: FirebaseUser) {


    }
}