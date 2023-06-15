package com.example.Maatran.tests

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.Maatran.R
import com.example.Maatran.databinding.ActivityWelcomeScreenBinding
import com.example.Maatran.ui.SignUpActivity
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {

    private val _tag:String = "WelcomeActivity"
    private lateinit var _binding: ActivityWelcomeScreenBinding
    private lateinit var _signInBtn: MaterialButton
    private lateinit var _signUpBtn: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityWelcomeScreenBinding.inflate(layoutInflater)
        val rootView = _binding.root
        setContentView(rootView)
        window.statusBarColor = resources.getColor(R.color.welcome_accent)
        val testBtn: Button = _binding.testBtn
        testBtn.visibility = View.VISIBLE
        testBtn.setOnClickListener {
        signInWithTestAccount()
        }
        _signInBtn = _binding.welcomeSigninBtn
        _signUpBtn = _binding.welcomeRegisterBtn
    }

    override fun onStart() {
        super.onStart()
        _signInBtn.setOnClickListener{ signInOptions()}
        _signInBtn.setOnTouchListener { v: View, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                v.background = AppCompatResources.getDrawable(
                    this,
                    R.drawable.welcome_button_clickedstate
                )
                changeTextColor(1, _signInBtn)
                return@setOnTouchListener true
            }
            if (event.action == MotionEvent.ACTION_UP) {
                v.performClick()
                v.background = AppCompatResources.getDrawable(
                    this,
                    R.drawable.welcome_button_nonclickedstate
                )
                changeTextColor(0, _signInBtn)
                return@setOnTouchListener true
            }
            false
        }
        _signUpBtn.setOnClickListener{ registerOptions()}
        _signUpBtn.setOnTouchListener{v, event ->
            if(event.action == MotionEvent.ACTION_DOWN)
            {
                v.background = AppCompatResources.getDrawable(
                    this,
                    R.drawable.welcome_button_clickedstate
                )
                changeTextColor(1, _signUpBtn)
                return@setOnTouchListener true
            }
            if(event.action == MotionEvent.ACTION_UP)
            {
                v.performClick()
                v.background = AppCompatResources.getDrawable(
                    this,
                    R.drawable.welcome_button_nonclickedstate
                )
                changeTextColor(0, _signUpBtn)
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }
    }

    private fun changeTextColor(type: Int, btn: MaterialButton)
    {
        if(type == 1)
            btn.setTextColor(resources.getColor(R.color.white))
        else
            btn.setTextColor(resources.getColor(R.color.welcome_accent))
    }

    private fun signInOptions() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun registerOptions() {
        val intent = Intent(applicationContext, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun signInWithTestAccount() {
        val email = "test1@gmail.com"
        val password = "123456"
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(_tag, "signInWithEmail:success")
                    Toast.makeText(
                        this,
                        "WARNING: USE THIS FEATURE FOR DEBUG/TEST PURPOSES ONLY",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent =
                        Intent(applicationContext, AppNavigationActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(
                        _tag,
                        "signInWithEmail:failure",
                        task.exception
                    )
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}