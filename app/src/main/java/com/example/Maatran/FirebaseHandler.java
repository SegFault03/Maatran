package com.example.Maatran;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class FirebaseHandler {

    private static final String TAG = "FirebaseHandler";
    private final FirebaseFirestore db;
    private String name, gender, address, mobile, emergency_no;
    private int age;

    public FirebaseHandler(String name, String gender, String address, String mobile, String emergency_no, int age)
    {
        db = FirebaseFirestore.getInstance();
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.mobile = mobile;
        this.emergency_no = emergency_no;
        this.address = address;
    }

    public void addDetails(FirebaseUser user)
    {
        Map<String, Object> details = new HashMap<>();
        details.put("name", name);
        details.put("age", age);
        details.put("gender", gender);
        details.put("address", address);
        details.put("mobile", mobile);
        details.put("emergency", emergency_no);

        db.collection("UserDetails").document(user.getEmail())
                .set(details, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }


}
