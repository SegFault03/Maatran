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

public class DashboardActivity extends AppCompatActivity {
    public static final String TAG = "DashboardActivity";
    ProgressDialog progressDialog;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_1);
        getSupportActionBar().hide();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data..");
        progressDialog.show();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        fetchUserDetails();
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
        intent.putExtra("isPatient", true);
        startActivity(intent);
    }

    public void fetchUserDetails()
    {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        TextView user_name = findViewById(R.id.dashboard_user_name);
        DocumentReference df= db.collection("UserDetails").document(user.getEmail());
        df.get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                DocumentSnapshot ds = task.getResult();
                if(ds.exists())
                    user_name.setText(ds.get("name").toString());
                else
                    Log.d(TAG, "No such document");
            }
            else
                Log.d(TAG, "get failed with ", task.getException());
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        });
    }
}