package com.example.Maatran;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//Quiz Activity
//called every time user needs to add/edit details
//receives a Bundle containing Boolean value from the intent calling it...
//..which specifies whether the user is adding or editing details of a user or of a patient
//xml file: screen_4
public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_4);
        getSupportActionBar().hide();
        Toast.makeText(DetailsActivity.this, "Enter your information.",
                Toast.LENGTH_SHORT).show();
    }

    public void saveBtn(View view)
    {
        String name = ((EditText)findViewById(R.id.name)).getText().toString();
        String gender = ((EditText) findViewById(R.id.gender)).getText().toString();
        String mobile = ((EditText) findViewById(R.id.mobile_number)).getText().toString();
        String emergency_mobile = ((EditText) findViewById(R.id.emergency_no)).getText().toString();
        String address = ((EditText) findViewById(R.id.address)).getText().toString();
        long age = Long.parseLong(((EditText) findViewById(R.id.age)).getText().toString());
        boolean isPatient = getIntent().getExtras().getBoolean("isPatient");        //get boolean value from intent
        //if user is adding details of a patient, isPatient is true
        //if user is adding details of a user, isPatient is false

        boolean flag=true;
        //check if all fields are filled and if all fields are valid
        if(mobile.length()!=10)
        {
            Toast.makeText(getApplicationContext(),"Enter the correct mobile number",Toast.LENGTH_SHORT).show();
            flag=false;
        }
        if(emergency_mobile.length()!=10)
        {
            Toast.makeText(getApplicationContext(), "Enter the correct emergency mobile number", Toast.LENGTH_SHORT).show();
            flag=false;
        }
        if(age<18||age>90)
        {
            Toast.makeText(getApplicationContext(), "Only users above or 18 and below 90 are allowed to register", Toast.LENGTH_SHORT).show();
            flag=false;
        }
        if(flag)
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            FirebaseHandler fdb = new FirebaseHandler(name, gender, address, mobile, emergency_mobile, age, isPatient);
            fdb.addDetails(user);

            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(intent);
        }
    }

    public void skip(View view)
    {
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(intent);
    }
}