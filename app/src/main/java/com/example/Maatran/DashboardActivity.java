package com.example.Maatran;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {
    public static final String TAG = "DashboardActivity";
    ArrayList<User> userArrayList;
    UserAdapter userAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_1);
        getSupportActionBar().hide();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data..");
        progressDialog.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        TextView user_name = findViewById(R.id.dashboard_user_name);
        DocumentReference df= db.collection("UserDetails").document(user.getEmail());
        df.get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                DocumentSnapshot ds = task.getResult();
                if(ds.exists())
                {
                    user_name.setText(ds.get("name").toString());
                }
                else
                {
                    Log.d(TAG, "No such document");
                }
            }
            else
            {
                Log.d(TAG, "get failed with ", task.getException());
            }
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        });
    }

    /*public void showPatients(FirebaseUser user)
    {
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        DocumentReference docRef=db.collection("UserDetails").document(user.getEmail()).collection("Patients").document("abc");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    TextView tv_name =  findViewById(R.id.patient_name_1);
                    tv_name.setText("NAME: "+document.toObject(User.class).getName());
                    TextView tv_age =  findViewById(R.id.patient_age_1);
                    tv_age.setText("AGE: "+document.toObject(User.class).getAge());
                    TextView user_name =  findViewById(R.id.dashboard_user_name);
                    user_name.setText(document.toObject(User.class).getName());
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData().get("address"));
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }*/


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
        intent.putExtra("isPatient", true);
        startActivity(intent);
    }
}