package com.example.Maatran;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {
    public static final String TAG = "DashboardActivity";
    ProgressDialog progressDialog;
    FirebaseUser user;
    boolean isPatient = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_1);

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
        intent.putExtra("isPatient", isPatient);
        startActivity(intent);
    }

    public void userProfileView(View view)
    {
        Intent intent = new Intent(getApplicationContext(),ProfileView.class);
        startActivity(intent);
    }

    public void addPatient(View view)
    {
        Intent intent = new Intent(getApplicationContext(),EditPatient.class);
        intent.putExtra("isPatient", true);
        intent.putExtra("newDetails", true);
        startActivity(intent);
    }

    public void fetchUserDetails()
    {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        TextView user_name = findViewById(R.id.dashboard_user_name);
        DocumentReference df= db.collection("UserDetails").document(Objects.requireNonNull(user.getEmail()));
        df.get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                DocumentSnapshot ds = task.getResult();
                if(ds.exists()) {
                    user_name.setText(Objects.requireNonNull(ds.get("name")).toString());
                    if(Objects.requireNonNull(ds.get("isWorker")).toString().equals("true")) {
                        findViewById(R.id.report_button).setVisibility(View.GONE);
                        findViewById(R.id.bluetooth_test_btn).setVisibility(View.GONE);
                        isPatient = false;
                    }
                    else
                    {
                        Button viewFamilybtn = findViewById(R.id.viewUsersBtn);
                        Button addFamilybtn = findViewById(R.id.report_button);
                        viewFamilybtn.setText("VIEW FAMILY");
                        addFamilybtn.setText("ADD A FAMILY MEMBER");
                    }
                }
                else
                    Log.d(TAG, "No such document");
            }
            else
                Log.d(TAG, "get failed with ", task.getException());
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        });
    }

    public void bluetoothService(View view)
    {
        Intent intent = new Intent(getApplicationContext(),BluetoothActivity.class);
        startActivity(intent);
    }
}