package com.example.Maatran;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardActivity extends AppCompatActivity {
    public static final String TAG = "DashboardActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_1);
        getSupportActionBar().hide();
        Button reports = (Button) findViewById(R.id.report_button);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        showPatients(user);

        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //replace following code with reports activity
                Intent intent = new Intent(getApplicationContext(), ReportsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showPatients(FirebaseUser user)
    {
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        DocumentReference docRef=db.collection("UserDetails").document(user.getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        TextView tv_name = (TextView) findViewById(R.id.patient_name_1);
                        tv_name.setText("NAME: "+document.getData().get("name").toString());
                        TextView tv_age = (TextView) findViewById(R.id.patient_age_1);
                        tv_age.setText("AGE: "+document.getData().get("age").toString());
                        TextView user_name = (TextView) findViewById(R.id.dashboard_user_name);
                        user_name.setText(document.getData().get("name").toString());
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

    public void showReports(View view)
    {
        Intent intent = new Intent(getApplicationContext(), ReportsActivity.class);
        startActivity(intent);
    }

    public void userProfileView(View view)
    {
        Intent intent = new Intent(getApplicationContext(),ProfileView.class);
        startActivity(intent);
    }
}
