package com.example.Maatran.services

import android.util.Log
import com.example.Maatran.services.FirebaseServices.Auth
import com.example.Maatran.tests.AppNavigationActivity
import com.example.Maatran.tests.WelcomeActivity
import com.example.Maatran.ui.EditPatientActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * This class is used for performing all the firebase related operations, such as checking if the user is already signed in or not,
 * checking if the user has completed his/her details or not, fetching the user details from the database, etc.
 * Contains a nested class [Auth] which is used for checking if the user is already signed in or not.
 * */
class FirebaseServices {

    /**
     * This class is used for checking if the user is already signed in or not, and for
     * checking if the user has completed his/her details or not.
     * Sends the response back to the class which called [checkForExistingUser] using [handleResponse].
     * @property handleResponse a lambda function which takes an integer as an argument.
     * The integer returned by handleResponse is the status code.
     * Please define inside the lambda function what to do with the status code.
     * */
    class Auth(val handleResponse: (Int) -> Unit){
        private val _tag = "FirebaseServices.Auth"
        private val _mAuth = FirebaseAuth.getInstance()
        /**
         * Used to check if the user is already signed in or not.
         * If the user is signed in, then check if the user has completed his/her details or not.
         * Sends the response back to the class which called [checkForExistingUser] using [handleResponse].
         * StatusCode:
         * 1 if everything is well and right (go to [AppNavigationActivity]),
         * 0 if user exists but has incomplete user details (go to [EditPatientActivity]),
         * -1 if user does not exist/not signed in (go to [WelcomeActivity])
         */
        fun checkForExistingUser()
        {
            val user = _mAuth.currentUser
            if (user == null) {
                handleResponse(-1)
            } else {
                val userMailId = user.email
                if (userMailId != null) {
                    checkForUserDetails(userMailId)
                }
            }
        }

        /**
         * Attempts to sign in the user with the given email id and password.
         * Ultimately calls [checkForExistingUser] to check if the user has completed his/her details or not.
         * Response is handled by [handleResponse].
         * Please check the validity of the email id and password before calling this function.
         * @param email The email id of the user
         * @param pwd The password of the user
         * */
        fun signInWithEmailAndPassword(email: String, pwd: String)
        {
            _mAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        Log.d(_tag, "signInWithEmail:success")
                        val user = _mAuth.currentUser
                        val userMailId = user?.email
                        if (userMailId != null) {
                            checkForUserDetails(userMailId)
                        }
                    }
                    else
                    {
                        Log.w(_tag, "signInWithEmail:failure", it.exception)
                        handleResponse(-1)
                    }
                }
        }


        /**
         * Used to check if the user has completed his/her details or not.
         * @param userMailId The email id of the user
         * @return 1, if the user has completed his/her details, 0 if the user has not completed his/her details, -1 if the user does not exist.
         * [checkForExistingUser] calls this function, and is therefore private
         * Use [checkForExistingUser] to call this function
         */
        private fun checkForUserDetails(userMailId: String) {
            val db = Firebase.firestore
            var statusCode: Int
            val docRef = db.collection("UserDetails").document(userMailId)
            docRef.get()
                .addOnSuccessListener { document ->
                    statusCode = if (document != null) {
                        Log.d(_tag, "DocumentSnapshot data: ${document.data}")
                        if(document["name"]==null) 0 else 1
                    } else {
                        Log.d(_tag, "Document does not exist")
                        -1
                    }
                    handleResponse(statusCode)
                }
                .addOnFailureListener { exception ->
                    Log.d(_tag, "get failed with ", exception)
                    handleResponse(-1)
                }
        }
    }



}