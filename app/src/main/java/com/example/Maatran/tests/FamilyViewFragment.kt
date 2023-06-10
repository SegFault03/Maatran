package com.example.Maatran.tests

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Maatran.R
import com.example.Maatran.databinding.FragmentFamilyViewBinding
import com.example.Maatran.services.User
import com.example.Maatran.services.UserAdapter
import com.example.Maatran.ui.ReportsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FamilyViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FamilyViewFragment : Fragment(), UserAdapter.OnPatientListener {
    var recyclerView: RecyclerView? = null
    var options_layout: LinearLayout? = null
    var userArrayList: ArrayList<User>? = null
    var userId: ArrayList<String>? = null
    var userAdapter: UserAdapter? = null
    var db: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    var spinner_locality: Spinner? = null
    var locality = "Howrah"
    var family_id = ""
    var email = ""
    var isPatient = false
    var isSuccessful = false
    private var _binding: FragmentFamilyViewBinding? = null
    val binding get() = _binding!!
    private var f = 0
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isPatient = requireActivity().intent.getBooleanExtra("isPatient", true)
        db = FirebaseFirestore.getInstance()
        user = FirebaseAuth.getInstance().currentUser
        userId = ArrayList()
        userArrayList = ArrayList()
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume() {
        super.onResume()
        if(isPatient)
            fetchDetails()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFamilyViewBinding.inflate(inflater, container, false)
        return binding.root
//        return inflater.inflate(R.layout.fragment_family_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.joinFamily.setOnClickListener { view -> joinFamily(view) }
        binding.createFamily.setOnClickListener { view -> familyKeyHandler(view) }
        spinner_locality = binding.localitySpinner
        options_layout = binding.optionsLayout
        val loc_adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.localities,
            android.R.layout.simple_spinner_item
        )
        loc_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner_locality!!.adapter = loc_adapter
        if (isPatient) spinner_locality!!.visibility = View.GONE else options_layout!!.visibility =
            View.GONE

        recyclerView = binding.recyclerView
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        userAdapter = UserAdapter(
            requireContext(),
            userArrayList,
            this
        ) //Creates a new Adapter with current Activity as context
        recyclerView!!.adapter = userAdapter

        spinner_locality!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                locality = adapterView.getItemAtPosition(i) as String
                EventChangeListener2()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    fun fetchDetails() {
        val ref = db!!.collection("UserDetails").document(user!!.email!!)
        ref.get().addOnSuccessListener { value: DocumentSnapshot ->
            if (value.get("admin_id").toString() == "null") {
                recyclerView!!.visibility = View.INVISIBLE
                f = 1
            } else {
                recyclerView!!.visibility = View.VISIBLE
                val tv: TextView = binding.createFamilyTxt
                tv.text = "GET KEY"
                val join_family_layout: ConstraintLayout = binding.joinFamilyLayout
                join_family_layout.visibility = View.GONE
                f = 0
                EventChangeListener()
            }
        }
    }

    fun joinFamily(view: View) {
        val inflater = layoutInflater
        val popupFamilyDetails = inflater.inflate(R.layout.family_details, null)
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        // lets taps outside the popupWindow dismiss it
        val popupWindow = PopupWindow(popupFamilyDetails, width, height, true)
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        val join = popupFamilyDetails.findViewById<Button>(R.id.btn_join)
        val cancel = popupFamilyDetails.findViewById<Button>(R.id.btn_cancel)
        join.setOnClickListener { v: View? ->
            val email =
                (popupFamilyDetails.findViewById<View>(R.id.admin_id) as EditText).text
                    .toString().trim { it <= ' ' }
            val family_id =
                (popupFamilyDetails.findViewById<View>(R.id.family_key) as EditText).text
                    .toString().trim { it <= ' ' }
            if (email === "" || family_id === "") {
                Toast.makeText(
                    context,
                    "You must enter a valid admin email and family key.",
                    Toast.LENGTH_SHORT
                ).show()
                popupWindow.dismiss()
                return@setOnClickListener
            }
            val ref = db!!.collection("UserDetails")
                .document(email!!)
            val mp =
                HashMap<String, String?>()
            mp["email"] = user!!.email
            ref.get()
                .addOnSuccessListener { value: DocumentSnapshot ->
                    if (value.data!!["family_id"].toString() == family_id
                    ) {
                        ref.collection("Patients").document(user!!.email!!)
                            .set(mp)
                            .addOnSuccessListener { aVoid: Void? ->
                                Log.d(
                                    "TAG",
                                    "DocumentSnapshot successfully written!"
                                )
                            }
                            .addOnFailureListener { e: java.lang.Exception? ->
                                Log.w(
                                    "TAG",
                                    "Error writing document",
                                    e
                                )
                            }
                        mp.clear()
                        mp["admin_id"] = email
                        mp["family_id"] = family_id
                        db!!.collection("UserDetails")
                            .document(user!!.email!!)
                            .set(mp, SetOptions.merge())
                            .addOnSuccessListener { aVoid: Void? ->
                                Log.d(
                                    "TAG",
                                    "DocumentSnapshot successfully written!"
                                )
                            }
                            .addOnFailureListener { e: java.lang.Exception? ->
                                Log.w(
                                    "TAG",
                                    "Error writing document",
                                    e
                                )
                            }
                    } else Toast.makeText(
                        context,
                        "Invalid credentials.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            f = 0
            popupWindow.dismiss()
            fetchDetails()
        }
        cancel.setOnClickListener { v: View? -> popupWindow.dismiss() }
    }

    fun familyKeyHandler(view: View) {
        if (f == 1) createFamily(view) else {
            val ref = db!!.collection("UserDetails").document(user!!.email!!)
            ref.get().addOnSuccessListener { value: DocumentSnapshot ->
                displayKey(
                    value["family_id"].toString(),
                    view
                )
            }
        }
    }

    fun displayKey(key: String?, view: View?) {
        val inflater = layoutInflater
        val popupCopyKey = inflater.inflate(R.layout.copy_key, null)
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        // lets taps outside the popupWindow dismiss it
        val popupWindow = PopupWindow(popupCopyKey, width, height, true)
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        val tv = popupCopyKey.findViewById<TextView>(R.id.family_key)
        tv.text = key
        val copy = popupCopyKey.findViewById<Button>(R.id.btn_copy)
        val cancel = popupCopyKey.findViewById<Button>(R.id.btn_cancel)
        copy.setOnClickListener { v: View? ->
            val clipboard =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", key)
            clipboard.setPrimaryClip(clip)
            popupWindow.dismiss()
        }
        cancel.setOnClickListener { v: View? -> popupWindow.dismiss() }
    }

    fun createFamily(view: View) {
        val family_id = UUID.randomUUID().toString()
        displayKey(family_id, view)
        val mp = HashMap<String, String?>()
        val ref = db!!.collection("UserDetails").document(user!!.email!!)
        mp["admin_id"] = user!!.email
        mp["family_id"] = family_id
        ref.set(mp, SetOptions.merge())
            .addOnSuccessListener { aVoid: Void? ->
                Log.d(
                    "TAG",
                    "DocumentSnapshot successfully written!"
                )
            }
            .addOnFailureListener { e: Exception? ->
                Log.e(
                    "TAG",
                    "Error writing document",
                    e
                )
            }
        mp.clear()
        mp["email"] = user!!.email
        ref.collection("Patients")
            .document(user!!.email!!)
            .set(mp)
            .addOnSuccessListener { aVoid: Void? ->
                Log.d(
                    "TAG",
                    "DocumentSnapshot successfully written!"
                )
            }
            .addOnFailureListener { e: Exception? ->
                Log.e(
                    "TAG",
                    "Error writing document",
                    e
                )
            }
        f = 0
        fetchDetails()
    }

    private fun EventChangeListener() {
        userArrayList!!.clear() //clears the existing PatientDetails and fills it up with new and updated data
        val ref = db!!.collection("UserDetails").document(user!!.email!!)
        ref.get().addOnSuccessListener { value: DocumentSnapshot ->
            db!!.collection("UserDetails")
                .document(
                    Objects.requireNonNull<Any?>(
                        Objects.requireNonNull<Map<String, Any>?>(
                            value.data
                        )["admin_id"]
                    ).toString()
                )
                .collection("Patients")
                .get()
                .addOnSuccessListener { v: QuerySnapshot ->
                    for (dc in v.documents) {
                        db!!.collection("UserDetails").document(dc.id)
                            .get()
                            .addOnSuccessListener { doc: DocumentSnapshot ->
                                userArrayList!!.add(
                                    doc.toObject(
                                        User::class.java
                                    )!!
                                ) //Converting the DocuSnapshot to a User.class object
                                userId!!.add(dc.id)
                                Log.v("TAG", dc.id)
                                userAdapter!!.notifyDataSetChanged()
                            }
                    }
                }
        }
    }

    private fun EventChangeListener2() {
        userArrayList!!.clear() //clears the existing PatientDetails and fills it up with new and updated data
        val ref = db!!.collection("UserDetails")
        if (locality != "Show all") {
            ref.whereEqualTo("isWorker", "false").whereEqualTo("locality", locality).get()
                .addOnSuccessListener { value: QuerySnapshot ->
                    for (dc in value.documents) {
                        userArrayList!!.add(dc.toObject(User::class.java)!!) //Converting the DocuSnapshot to a User.class object
                        userId!!.add(dc.id)
                    }
                    userAdapter!!.notifyDataSetChanged()
                }
        } else {
            ref.whereEqualTo("isWorker", "false").get()
                .addOnSuccessListener { value2: QuerySnapshot ->
                    for (ds in value2.documents) {
                        userArrayList!!.add(ds.toObject(User::class.java)!!) //Converting the DocuSnapshot to a User.class object
                        userId!!.add(ds.id)
                    }
                    userAdapter!!.notifyDataSetChanged()
                }
        }
    }

    override fun onPatientClick(position: Int) {
        val intent = Intent(requireContext(), ReportsActivity::class.java)
        intent.putExtra(
            "user",
            userArrayList!![position]
        ) //passes a User object by encoding it in a Parcel
        intent.putExtra("id", userId!![position])
        startActivity(intent)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FamilyViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FamilyViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}