package com.example.Maatran;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class FirebaseHandler {

    private static final String TAG = "FirebaseHandler";
    private final FirebaseFirestore db;
    private String name, gender, address, mobile, emergency_no;
    private long age;
    private boolean isPatient;

    public FirebaseHandler(String name, String gender, String address, String mobile, String emergency_no, long age, boolean isPatient)
    {
        db = FirebaseFirestore.getInstance();
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.mobile = mobile;
        this.emergency_no = emergency_no;
        this.address = address;
        this.isPatient = isPatient;
    }

    public void addDetails(FirebaseUser user)
    {
        User details = new User(name, age, gender, mobile, address, emergency_no);
        if(isPatient)
        {
            db.collection("UserDetails").document(user.getEmail()).collection("Patients").document(name)
                    .set(details, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
        }
        else {
            db.collection("UserDetails").document(user.getEmail())
                    .set(details, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
        }
    }


}
