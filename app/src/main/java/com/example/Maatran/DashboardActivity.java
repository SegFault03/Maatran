package com.example.Maatran;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_1);
        getSupportActionBar().hide();
        Button reports = (Button) findViewById(R.id.report_button);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //replace following code with reports activity
                setContentView(R.layout.sensors_home);
            }
        });
    }

    public void userProfileView(View view)
    {
        Intent intent = new Intent(getApplicationContext(),ProfileView.class);
        startActivity(intent);
    }
}
