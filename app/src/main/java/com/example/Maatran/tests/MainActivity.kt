package com.example.Maatran.tests

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.Maatran.databinding.ActivityMainBinding
import com.example.Maatran.ui.EditPatientActivity
import com.example.Maatran.utils.commonUIFunctions
import com.example.Maatran.services.FirebaseServices

class MainActivity : AppCompatActivity(), commonUIFunctions {
    private lateinit var _binding: ActivityMainBinding

    private val _tag = "MainActivity"

    private var _lastUpdatedDot: Int = 0

    private lateinit var _loadingDotHandler: Handler

    private lateinit var _signInHandler: Handler

    private var _signInRunnable: Runnable = Runnable {
        Toast.makeText(this,"Its taking too long, please log-in/sign-up manually",Toast.LENGTH_SHORT).show()
        val intent = Intent(applicationContext, WelcomeActivity::class.java)
        startActivity(intent)
        super.finish()
    }
    private val _loadingDotRunnable: Runnable = Runnable { callLoadingDotAnim() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val rootView = _binding.root
        setContentView(rootView)
        _loadingDotHandler = Handler(Looper.getMainLooper())
        _loadingDotHandler.postDelayed(_loadingDotRunnable,500)
        checkForSignedInUser()
        _signInHandler = Handler(Looper.getMainLooper())
        _signInHandler.postDelayed(_signInRunnable,8000)
    }

    private fun callLoadingDotAnim()
    {
       _lastUpdatedDot =  changeLoadingDotAnimation(_lastUpdatedDot, this)
        _loadingDotHandler.postDelayed(_loadingDotRunnable, 500)
    }

    private fun checkForSignedInUser()
    {
        val firebaseServiceAuth: FirebaseServices.Auth = FirebaseServices.Auth{
            Log.v(_tag, it.toString())
            when(it)
            {
                1 -> {
                    val intent = Intent(applicationContext,AppNavigationActivity::class.java)
                    startActivity(intent)
                    super.finish()
                }
                0 -> {
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
                -1 -> {
                    val intent = Intent(applicationContext, WelcomeActivity::class.java)
                    startActivity(intent)
                    super.finish()
                }
                else -> {}
            }
        }
        firebaseServiceAuth.checkForExistingUser()
    }

    override fun onDestroy() {
        super.onDestroy()
        _signInHandler.removeCallbacks(_signInRunnable)
        _loadingDotHandler.removeCallbacks(_loadingDotRunnable)
    }
}