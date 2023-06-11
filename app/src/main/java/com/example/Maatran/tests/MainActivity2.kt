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
import com.example.Maatran.ui.EditPatientActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity2 : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding

    private val _tag = "MainActivity2"

    private var _lastUpdatedDot: Int = 0

    private lateinit var _loadingDotHandler: Handler

    private lateinit var _signInHandler: Handler

    private var _signInRunnable: Runnable = Runnable {
        Toast.makeText(this,"Its taking too long, please log-in/sign-up manually",Toast.LENGTH_SHORT).show()
        val intent = Intent(applicationContext, WelcomeActivityNew::class.java)
        startActivity(intent)
        super.finish()
    }
    private val _loadingDotRunnable: Runnable = Runnable { changeLoadingDot() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val rootView = _binding.root
        setContentView(rootView)
        _loadingDotHandler = Handler(Looper.getMainLooper())
        _loadingDotHandler.postDelayed(_loadingDotRunnable,500)
        signInOptions()
        _signInHandler = Handler(Looper.getMainLooper())
        _signInHandler.postDelayed(_signInRunnable,8000)
    }

    private fun changeLoadingDot()
    {
        val currLoadDotView = "load_dot" + _lastUpdatedDot % 3
        val oldLoadDotView = "load_dot" + if (_lastUpdatedDot % 3 == 0) 2 else _lastUpdatedDot % 3 - 1
        _lastUpdatedDot++
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
        _loadingDotHandler.postDelayed(_loadingDotRunnable, 500)
    }

    private fun signInOptions() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            val intent = Intent(applicationContext, WelcomeActivityNew::class.java)
            startActivity(intent)
            super.finish()
        } else {
            val userMailId = user.email
            if (userMailId != null) {
                checkForUserDetails(userMailId)
            }
        }
    }

    private fun checkForUserDetails(userMailId: String) {
        val db = Firebase.firestore
        val docRef = db.collection("UserDetails").document(userMailId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(_tag, "DocumentSnapshot data: ${document.data}")
                    if(document["name"]!=null)
                    {
                        val intent = Intent(applicationContext,AppNavigationActivity::class.java)
                        startActivity(intent)
                        super.finish()
                    }
                    else
                    {
                        Toast.makeText(
                            this,
                            "Fill in your details to proceed...",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(applicationContext, EditPatientActivity::class.java)
                        intent.putExtra("isPatient", false)
                        intent.putExtra("newDetails", true)
                        startActivity(intent)
                        super.finish()
                    }
                } else {
                    Log.d(_tag, "Document does not exist")
                    Toast.makeText(this, "Please log in manually", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, WelcomeActivityNew::class.java)
                    startActivity(intent)
                    super.finish()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(_tag, "get failed with ", exception)
                Toast.makeText(this, "Please log in manually", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, WelcomeActivityNew::class.java)
                startActivity(intent)
                super.finish()
            }

    }

    override fun onDestroy() {
        super.onDestroy()
        _signInHandler.removeCallbacks(_signInRunnable)
        _loadingDotHandler.removeCallbacks(_loadingDotRunnable)
    }
}