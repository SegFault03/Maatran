package com.example.maatran;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    public FirebaseHandler()
    {
        db = FirebaseFirestore.getInstance();
        this.name = "lalalala";
        this.age = 0;
        this.gender = "";
        this.mobile = "";
        this.emergency_no = "";
        this.address = "";
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
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


}
