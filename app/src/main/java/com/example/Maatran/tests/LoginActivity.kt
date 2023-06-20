package com.example.Maatran.tests

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.Maatran.R
import com.example.Maatran.databinding.ActivityLoginScreenBinding
import com.example.Maatran.services.FirebaseServices
import com.example.Maatran.ui.EditPatientActivity
import com.example.Maatran.ui.ResetPasswordActivity
import com.example.Maatran.utils.commonUIFunctions
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity(), commonUIFunctions {
    private lateinit var _binding:ActivityLoginScreenBinding
    private lateinit var loadingAnimContainerLayout: LinearLayout
    private var lastUpdatedDot: Int = 0
    private lateinit var loadingAnimHandler: Handler
    private val loadingAnimRunnable: Runnable = Runnable { callLoadingDotAnim()}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        val rootView = _binding.root
        setContentView(rootView)
        window.statusBarColor = android.graphics.Color.parseColor("#FFAFCC")
        loadingAnimContainerLayout = _binding.loadingAnimation
        loadingAnimContainerLayout.visibility = View.GONE
        val loginBtn: MaterialButton = _binding.loginSigninbtn
        val backBtn: ImageView = _binding.backBtn
        backBtn.setOnClickListener {
            super.finish()
        }

        loginBtn.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_DOWN)
            {
                v.setBackgroundColor(resources.getColor(R.color.white))
                changeTextColor(1)
                return@setOnTouchListener true
            }
            if(event.action==MotionEvent.ACTION_UP)
            {
                v.performClick()
                v.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                changeTextColor(0)
                return@setOnTouchListener true
            }
            false
        }

        loginBtn.setOnClickListener {
            val email: String = _binding.loginEmailEdit.text.toString()
            val pwd: String = _binding.loginPwdEdit.text.toString()
            val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$")
            if(email.matches(emailRegex) && pwd.isNotEmpty())
                attemptSigningIn(email,pwd)
            else
                Toast.makeText(applicationContext, "Please fill in the email and password correctly!", Toast.LENGTH_SHORT).show()
        }

        val forgotPwdText: TextView = _binding.loginForgotpwd
        forgotPwdText.setOnClickListener{
            startActivity(Intent(applicationContext, ResetPasswordActivity::class.java))
            super.finish()
        }
        val signUpBtn: TextView = _binding.loginSignupOption
        signUpBtn.setOnClickListener{
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
            super.finish()
        }
        loadingAnimHandler = Handler(Looper.getMainLooper())
        loadingAnimHandler.postDelayed(loadingAnimRunnable, 300)
    }

    private fun callLoadingDotAnim() {
        lastUpdatedDot = changeLoadingDotAnimation(lastUpdatedDot, this)
        loadingAnimHandler.postDelayed(loadingAnimRunnable, 300)
    }

    private fun changeTextColor(type: Int) {
        val btn = _binding.loginSigninbtn
        if (type == 1) btn.setTextColor(resources.getColor(R.color.colorPrimary)) else btn.setTextColor(
            resources.getColor(R.color.white)
        )
    }
    private fun attemptSigningIn(email: String, pwd: String)
    {
        loadingAnimContainerLayout.visibility = View.VISIBLE
        val firebaseServices: FirebaseServices.Auth = FirebaseServices.Auth{
            when(it)
            {
                1 -> {
                    startActivity(Intent(applicationContext,AppNavigationActivity::class.java))
                    //TODO IMPLEMENT PLACEHOLDER MSG PASSING
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

                else -> {
                    Toast.makeText(applicationContext,"Authentication failed",Toast.LENGTH_SHORT).show()
                    loadingAnimContainerLayout.visibility = View.GONE
                }
            }
        }
        firebaseServices.signInWithEmailAndPassword(email,pwd)
    }
}