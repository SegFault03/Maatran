package com.example.Maatran.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.example.Maatran.R
import com.example.Maatran.databinding.FragmentSignupBinding
import com.example.Maatran.services.FirebaseServices
import com.google.firebase.auth.FirebaseAuth


class SignUpFragment : Fragment(R.layout.fragment_signup) {
    // TODO: Rename and change types of parameters
    private var isWorker = false
    private lateinit var _binding: FragmentSignupBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            isWorker = requireArguments().getBoolean(IS_WORKER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSignupBinding.inflate(inflater,container,false)
        val rootView = _binding.root
        if (!isWorker) {
            _binding.signupEmpidBox.visibility = View.GONE
            _binding.signupHospitalBox.visibility = View.GONE
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val signUpBtn = _binding.signupBtn
        signUpBtn.setOnTouchListener { v, event ->
            if (event.action== MotionEvent.ACTION_DOWN)
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
        signUpBtn.setOnClickListener { createNewUserAccount() }

    }

        private fun changeTextColor(type: Int) {
        val btn = _binding.signupBtn
        if (type == 1) btn.setTextColor(resources.getColor(R.color.colorPrimary)) else btn.setTextColor(
            resources.getColor(R.color.white)
        )
    }

    private fun createNewUserAccount()
    {
        val email: String = _binding.signupEmailEdit.text.toString()
        val pwd: String = _binding.signupPwdEdit.text.toString()
        val confirmPwd: String = _binding.signupPwdConfirmEdit.text.toString()
        val hospitalName: String? = _binding.signupHospitalEdit.text?.toString()
        val empId: String? = _binding.signupEmpidEdit.text?.toString()

        if(checkValidity(email, pwd, confirmPwd, hospitalName, empId))
        {
            //Notify parent activity that account creation process has started
            setFragmentResult("OnStartAccountCreation", bundleOf("hasStarted" to true))

            //Details valid, create account
            val firebaseServices = FirebaseServices.Auth{
                if(it==1)
                {
                    //Account creation successful, put initial user details
                    val currUser = FirebaseAuth.getInstance().currentUser
                    val firestoreServices = FirebaseServices.FireStore{it2 ->
                        if(it2==-1)
                        {
                            //Account creation unsuccessful, delete user account
                            val tempObject = FirebaseServices.Auth{ it3 -> Log.d("SignUpFragment", it3.toString())}
                            //PRAY THIS DOESN'T FAIL
                            tempObject.deleteUserAccount(currUser)
                        }
                        //signal to parent Activity, letting it know the results of the operation
                        setFragmentResult("SignUpResponse", bundleOf("response" to it2))
                    }
                    if(isWorker && !hospitalName.isNullOrBlank() && !empId.isNullOrBlank())
                        firestoreServices.putInitWorkerDetails(currUser,hospitalName,empId)
                    else
                        firestoreServices.putInitPatientDetails(currUser)
                }
                //signal to parent Activity, letting it know account creation was unsuccessful
                else
                    setFragmentResult("SignUpResponse", bundleOf("response" to it))
            }
            firebaseServices.createAccountWithEmailAndPassword(email,pwd)
        }
    }

    private fun checkValidity(email: String, pwd: String, confirmpwd: String, hospitalName: String? = "", empId:String? = ""): Boolean
    {
        if(email.isBlank() || pwd.isBlank())
        {
            Toast.makeText(context, "Either password or email field or both empty!", Toast.LENGTH_SHORT).show()
            return false
        }
        if(!email.matches(Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$")))
        {
            Toast.makeText(context, "Enter a valid email id", Toast.LENGTH_SHORT).show()
            return false
        }
        if(pwd.length<6)
        {
            Toast.makeText(context, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            return false
        }
        if(confirmpwd != pwd)
        {
            Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show()
            return false
        }
        if(isWorker && (hospitalName.isNullOrBlank()  || empId.isNullOrBlank()))
        {
            Toast.makeText(context, "Please complete the necessary fields", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    companion object {
        private const val IS_WORKER = "isWorker"
        @JvmStatic
        fun newInstance(isWorker: Boolean): SignUpFragment {
            val fragment = SignUpFragment()
            val args = Bundle()
            args.putBoolean(IS_WORKER, isWorker)
            fragment.arguments = args
            return fragment
        }
    }
}