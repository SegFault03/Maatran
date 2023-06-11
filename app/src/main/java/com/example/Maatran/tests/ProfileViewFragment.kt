package com.example.Maatran.tests

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.Maatran.R
import com.example.Maatran.databinding.FragmentProfileViewBinding
import com.example.Maatran.ui.ProfileViewActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileViewFragment : Fragment() {
    var view_profile: LinearLayout? = null
    private var _binding: FragmentProfileViewBinding? = null
    val binding get() = _binding!!
    var isPatient: Boolean = false
    var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = FirebaseAuth.getInstance().currentUser
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view_profile = binding.viewProfile
        view_profile!!.setOnClickListener { view ->
            run {
                val intent = Intent(
                    requireContext(),
                    ProfileViewActivity::class.java
                )
                startActivity(intent)
            }
        }
        binding.signOut.setOnClickListener { view -> signOut(view) }
        binding.userMailid.text = user!!.email
    }

    fun signOut(view: View) {
        val popupConfirmSignOut: View =
            layoutInflater.inflate(R.layout.popupview_confirmation, null)
        (popupConfirmSignOut.findViewById<View>(R.id.text_dialog) as TextView).text =
            "Do you really want to sign out?"
        val popupWindow = PopupWindow(
            popupConfirmSignOut,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        popupConfirmSignOut.findViewById<View>(R.id.btn_yes).setOnClickListener { v: View? ->
            FirebaseAuth.getInstance().signOut()
            val toast = Toast.makeText(
                context,
                "You have successfully signed out, redirecting you to the log-in page",
                Toast.LENGTH_SHORT
            )
            toast.show()
            val intent = Intent(requireContext(), WelcomeActivityNew::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().finish()
        }
        popupConfirmSignOut.findViewById<View>(R.id.btn_no)
            .setOnClickListener { v: View? -> popupWindow.dismiss() }
    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment ProfileViewFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            ProfileViewFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}