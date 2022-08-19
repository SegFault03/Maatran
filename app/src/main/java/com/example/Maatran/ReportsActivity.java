package com.example.Maatran;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReportsActivity extends AppCompatActivity {
    User user;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_sensor_information);
        getSupportActionBar().hide();

        user = getIntent().getParcelableExtra("user");
        userId = getIntent().getStringExtra("id");

        TextView name = findViewById(R.id.patient_name);
        name.setText("Name: "+user.getName());
        TextView age = findViewById(R.id.patient_age);
        age.setText("Age: "+user.getAge());
        TextView sex = findViewById(R.id.patient_sex);
        sex.setText("Sex: "+user.getGender());

    }

    public void editPatient(View view)
    {
        Intent intent = new Intent(getApplicationContext(), EditPatient.class);
        intent.putExtra("user", user);
        intent.putExtra("id", userId);
        startActivity(intent);
    }

    public void deletePatient(View view)
    {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("UserDetails")
                .document(mUser.getEmail())
                .collection("Patients")
                .document(userId);
        docRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("ReportsActivity", "DocumentSnapshot successfully deleted!");
            } else {
                Log.d("ReportsActivity", "Error deleting document", task.getException());
            }
        });
        super.finish();
    }

    public void backToHome(View view)
    {
        super.finish();
    }

}