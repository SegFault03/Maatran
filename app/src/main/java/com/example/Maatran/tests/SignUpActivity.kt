package com.example.Maatran.tests

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.Maatran.R
import com.example.Maatran.databinding.ActivitySignupScreenBinding
import com.example.Maatran.ui.EditPatientActivity
import com.example.Maatran.ui.SignUpFragment
import com.example.Maatran.utils.commonUIFunctions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class SignUpActivity : AppCompatActivity(), commonUIFunctions {
    private lateinit var _binding: ActivitySignupScreenBinding
    private lateinit var _fragmentManager: FragmentManager
//    private lateinit var signUpBtn: MaterialButton
//    private lateinit var signInOption: TextView
    private lateinit var loadingAnimContainer: LinearLayout
    private var tabSelected: Int = 0
    private var lastUpdatedDot: Int = 0
    private var isPatient = true
    private lateinit var loadingDotHandler: Handler
    private var loadingDotRunnable = Runnable {callLoadingDotAnim()}
    private lateinit var signUpFragment: SignUpFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignupScreenBinding.inflate(layoutInflater)
        val rootView = _binding.root
        setContentView(rootView)

        window.statusBarColor = resources.getColor(R.color.colorPrimary)
        _binding.backBtn.setOnClickListener {super.finish()}
        val signInOption = _binding.signupSigninOptions
        loadingAnimContainer = _binding.loadingAnimation
        loadingAnimContainer.visibility = View.GONE
        loadingDotHandler = Handler(Looper.getMainLooper())
        loadingDotHandler.postDelayed(loadingDotRunnable,300)

        _fragmentManager = supportFragmentManager

        supportFragmentManager
            .setFragmentResultListener("OnStartAccountCreation", this) { _, bundle ->
                val result = bundle.getBoolean("hasStarted")
                if(result)
                    loadingAnimContainer.visibility = View.VISIBLE
            }

        supportFragmentManager
            .setFragmentResultListener("SignUpResponse", this){ _, bundle ->
                loadingAnimContainer.visibility = View.GONE
                val response = bundle.getInt("response")
                if(response==1) {
                    Toast.makeText(
                        applicationContext,
                        "Account Creation Successful! Please fill in your details",
                        Toast.LENGTH_SHORT
                    ).show()
                    //TODO IMPLEMENT MESSAGE PASSING
                    val intent = Intent(applicationContext, EditPatientActivity::class.java)
                    intent.putExtra("isPatient", true)
                    intent.putExtra("newDetails", true)
                    startActivity(intent)
                    super.finish()
                }
                else
                    Toast.makeText(applicationContext, "Account Creation Unsuccessful! Please try again", Toast.LENGTH_SHORT).show()
            }

        val viewPager = _binding.viewPager2
        val viewPagerAdapter = ViewPagerAdapter(_fragmentManager, lifecycle)
        viewPager.adapter = viewPagerAdapter


//        if(savedInstanceState == null)
//        {
//            signUpFragment = SignUpFragment.newInstance(false)
//            _fragmentManager.beginTransaction().add(R.id.signupFragmentContainer, signUpFragment)
//                .setReorderingAllowed(true)
//                .commit()
//        }

        val tabLayout = _binding.tabLayout
        TabLayoutMediator(tabLayout,viewPager){ tab, position ->
            when(position)
            {
                0 -> tab.text = "Patient"
                1 -> tab.text = "Health Worker"
            }
        }.attach()
//        tabLayout.addOnTabSelectedListener( object: TabLayout.OnTabSelectedListener
//        {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                signUpFragment= SignUpFragment.newInstance(tab.position==1)
//                _fragmentManager.beginTransaction()
//                    .setCustomAnimations(
//                        R.anim.slide_in,  // enter
//                        R.anim.fade_out // exit
//                    )
//                    .replace(R.id.signupFragmentContainer, signUpFragment)
//                    .setReorderingAllowed(true)
//                    .commit()
//            }
//            override fun onTabUnselected(tab: TabLayout.Tab?) {}
//            override fun onTabReselected(tab: TabLayout.Tab?) {}
//        })

//        signUpBtn.setOnTouchListener { v, event ->
//            if (event.action==MotionEvent.ACTION_DOWN)
//            {
//                v.setBackgroundColor(resources.getColor(R.color.white))
//                changeTextColor(1)
//                return@setOnTouchListener true
//            }
//            if(event.action==MotionEvent.ACTION_UP)
//            {
//                v.performClick()
//                v.setBackgroundColor(resources.getColor(R.color.colorPrimary))
//                changeTextColor(0)
//                return@setOnTouchListener true
//            }
//            false
//        }
//        signUpBtn.setOnClickListener { signUpFragment.createNewUserAccount() }
        signInOption.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }
    }

//    private fun changeTextColor(type: Int) {
//        val btn = _binding.signupBtn
//        if (type == 1) btn.setTextColor(resources.getColor(R.color.colorPrimary)) else btn.setTextColor(
//            resources.getColor(R.color.white)
//        )
//    }

    private fun callLoadingDotAnim()
    {
        lastUpdatedDot = changeLoadingDotAnimation(lastUpdatedDot,this)
        loadingDotHandler.postDelayed(loadingDotRunnable, 300)
    }

}

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2 // Number of tabs/fragments

    override fun createFragment(position: Int): SignUpFragment {
        // Return the corresponding fragment based on the position
        return when (position) {
            0 -> SignUpFragment.newInstance(false)
            1 -> SignUpFragment.newInstance(true)
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}

