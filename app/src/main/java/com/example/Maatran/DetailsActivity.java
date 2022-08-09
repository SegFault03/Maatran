package com.example.Maatran;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//Quiz Activity
//called after EmailSignUp is complete
//xml file: screen_4
public class DetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_4);
        getSupportActionBar().hide();
        Toast.makeText(DetailsActivity.this, "Enter your information.",
                Toast.LENGTH_SHORT).show();

        ImageButton skip = (ImageButton) findViewById(R.id.skip_button);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(intent);
            }
        });

        ImageButton save = (ImageButton) findViewById(R.id.save_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ((EditText) findViewById(R.id.name)).getText().toString();
                String gender = ((EditText) findViewById(R.id.gender)).getText().toString();
                String mobile = ((EditText) findViewById(R.id.mobile_number)).getText().toString();
                String emergency_mobile = ((EditText) findViewById(R.id.emergency_no)).getText().toString();
                String address = ((EditText) findViewById(R.id.address)).getText().toString();
                int age = Integer.parseInt(((EditText) findViewById(R.id.age)).getText().toString());

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                FirebaseHandler fdb = new FirebaseHandler(name, gender, address, mobile, emergency_mobile, age);
                fdb.addDetails(user);

                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(intent);
            }
        });
    }
}
