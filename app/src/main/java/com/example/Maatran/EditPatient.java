package com.example.Maatran;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class EditPatient extends AppCompatActivity {

    User user;
    String id;
    private EditText name, age, gender, mobile, emergency, address;
    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_details);
        getSupportActionBar().hide();

        user = getIntent().getParcelableExtra("user");
        boolean isPatient = getIntent().getBooleanExtra("isPatient", true);

        name = findViewById(R.id.patient_name);
        name.setText(user.getName());
        age = findViewById(R.id.patient_age);
        age.setText(Long.toString(user.getAge()));
        gender = findViewById(R.id.patient_gender);
        gender.setText(user.getGender());
        mobile = findViewById(R.id.patient_mobile);
        mobile.setText(user.getMobile());
        emergency = findViewById(R.id.emergency_no);
        emergency.setText(user.getEmergency());
        address = findViewById(R.id.patient_address);
        address.setText(user.getAddress());

        if(isPatient) {
            id = getIntent().getStringExtra("id");
            docRef = FirebaseFirestore.getInstance()
                    .collection("UserDetails")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                    .collection("Patients")
                    .document(id);
        }
        else {
            docRef = FirebaseFirestore.getInstance()
                    .collection("UserDetails")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
    }

    public void saveDetails(View view)
    {
        user.setName(name.getText().toString());
        user.setGender(gender.getText().toString());
        user.setAge(Long.parseLong(age.getText().toString()));
        user.setAddress(address.getText().toString());
        user.setMobile(mobile.getText().toString());
        user.setAddress(address.getText().toString());

        docRef.set(user, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));

        super.finish();
    }

    public void cancelChanges(View view)
    {
        super.finish();
    }
}
