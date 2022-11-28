package com.example.Maatran;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ReportsActivity extends AppCompatActivity {
    User user;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_sensor_information);

        user = getIntent().getParcelableExtra("user");
        userId = getIntent().getStringExtra("id");

        DocumentReference df = FirebaseFirestore.getInstance().collection("UserDetails")
                .document(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()));
        df.get().addOnCompleteListener(task->{
            DocumentSnapshot ds = task.getResult();
            if(ds.get("isWorker").equals("true"))
            {
                findViewById(R.id.edit_patient).setVisibility(View.INVISIBLE);
                findViewById(R.id.delete_patient).setVisibility(View.INVISIBLE);
            }
        });

    }

    //For re-rendering ReportsActivity after Patient Details are edited
    @Override
    public void onResume()
    {
        super.onResume();
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
        assert mUser != null;
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("UserDetails")
                .document(Objects.requireNonNull(mUser.getEmail()))
                .collection("Patients")
                .document(userId);
        docRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("ReportsActivity", "DocumentSnapshot successfully deleted!");
                Toast toast = Toast.makeText(getApplicationContext(),"Patient data has been deleted",Toast.LENGTH_SHORT);
                toast.show();
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