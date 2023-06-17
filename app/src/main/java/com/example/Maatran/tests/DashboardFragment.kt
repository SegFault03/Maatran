package com.example.Maatran.tests

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.Maatran.R
import com.example.Maatran.databinding.FragmentDashboardBinding
import com.example.Maatran.services.UserModel
import com.example.Maatran.services.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private val TAG = "DashboardFragment"
    private lateinit var viewModel: UserViewModel

    private var _binding: FragmentDashboardBinding? = null
    val binding get() = _binding!!
    var isPatient: Boolean = false
    lateinit var email:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        email = FirebaseAuth.getInstance().currentUser!!.email.toString()

        viewModel =  ViewModelProvider(this)[UserViewModel::class.java]
        viewModel.fetchUser(email)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        //val source = Source.CACHE
//        db.collection("UserDetails").document(FirebaseAuth.getInstance().currentUser!!.email.toString())
//            .get()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val document = task.result
//                    binding.dashboardUserName.text = "Hello " + document.get("name").toString()
//                } else {
//                    Log.d(TAG, "Get failed: ", task.exception)
//                }
//            }
        viewModel.userData?.observe(viewLifecycleOwner, Observer {
            if(it!=null) {
                binding.dashboardUserName.text = "Hello " + (it as UserModel).name
                Log.v("TAG", it.toString())
            }
            else
                Toast.makeText(requireContext(), "Nah", Toast.LENGTH_SHORT).show()
        })
        //binding.dashboardUserName.text = "Hello " + viewModel.userData?.value?.name
    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment DashboardFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance() =
//            DashboardFragment().apply {
//                arguments = Bundle().apply {
//                    putParcelable("user", user)
//                }
//            }
//    }
}