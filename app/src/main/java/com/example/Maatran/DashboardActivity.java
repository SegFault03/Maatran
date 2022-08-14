package com.example.Maatran;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {
    public static final String TAG = "DashboardActivity";
    RecyclerView recyclerView;
    ArrayList<User> userArrayList;
    UserAdapter userAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_1);
        getSupportActionBar().hide();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        TextView user_name = (TextView) findViewById(R.id.dashboard_user_name);
        user_name.setText(user.getEmail());

        //showPatients(user);

    }

    public void showPatients(FirebaseUser user)
    {
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        DocumentReference docRef=db.collection("UserDetails").document(user.getEmail()).collection("Patients").document("abc");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        TextView tv_name = (TextView) findViewById(R.id.patient_name_1);
                        tv_name.setText("NAME: "+document.toObject(User.class).getName());
                        TextView tv_age = (TextView) findViewById(R.id.patient_age_1);
                        tv_age.setText("AGE: "+document.toObject(User.class).getAge());
                        TextView user_name = (TextView) findViewById(R.id.dashboard_user_name);
                        user_name.setText(document.toObject(User.class).getName());
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData().get("address"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    public void viewPatients(View view)
    {
        Intent intent = new Intent(getApplicationContext(), PatientsView.class);
        startActivity(intent);
    }

    public void userProfileView(View view)
    {
        Intent intent = new Intent(getApplicationContext(),ProfileView.class);
        startActivity(intent);
    }

    public void addPatient(View view)
    {
        Intent intent = new Intent(getApplicationContext(),DetailsActivity.class);
        startActivity(intent);
    }
}