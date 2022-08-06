package com.example.mumtran_login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


public class RegistrationActivity extends AppCompatActivity {

    EditText name, email, password, age, gender, mobile, address, confirm_password, org_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_2);
    }

    public void signUpAsPatient(View view) {
        setContentView(R.layout.screen_4);
        name = (EditText)findViewById(R.id.name);
        age = (EditText)findViewById(R.id.age);
        gender = (EditText)findViewById(R.id.gender);
        mobile = (EditText)findViewById(R.id.mobile_number);
        email = (EditText)findViewById(R.id.email);
        address = (EditText)findViewById(R.id.address);
        password = (EditText) findViewById(R.id.password);
        confirm_password = (EditText) findViewById(R.id.confirm_password);

        DBhelper DB = new DBhelper(this);

        ImageButton create = (ImageButton) findViewById(R.id.create_account);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SetValidation())
                {
                    String nameTXT = name.getText().toString();
                    String emailTXT = email.getText().toString();
                    String genderTXT = gender.getText().toString();
                    String mobileTXT = mobile.getText().toString();
                    String addressTXT = address.getText().toString();
                    int ageTXT = Integer.parseInt(age.getText().toString());

                    boolean checkEntry = DB.insertUserData(emailTXT, nameTXT, genderTXT, addressTXT, mobileTXT, ageTXT);
                    if(checkEntry) {
                        Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void signUpAsHW(View view) {
        setContentView(R.layout.screen_3);
        name = (EditText)findViewById(R.id.name);
        mobile = (EditText)findViewById(R.id.mobile_number);
        email = (EditText)findViewById(R.id.email);
        org_id = (EditText)findViewById(R.id.organisation_id);
        password = (EditText) findViewById(R.id.password);
        confirm_password = (EditText) findViewById(R.id.confirm_password);
        ImageButton create = (ImageButton) findViewById(R.id.create_acc);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetValidation();
            }
        });
    }

    public boolean SetValidation() {
        boolean isEmailValid, isPasswordValid, isNumberValid;
        // Check for a valid email address.
        if (email.getText().toString().isEmpty()) {
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            isEmailValid = false;
        } else  {
            isEmailValid = true;
        }

        // Check for a valid password.
        if (password.getText().toString().isEmpty()) {
            isPasswordValid = false;
        } else if (password.getText().length() < 6) {
            isPasswordValid = false;
        } else if(!password.getText().toString().equals(confirm_password.getText().toString())) {
            isPasswordValid = false;
        } else {
            isPasswordValid = true;
        }

        if(mobile.getText().length()!=10)
            isNumberValid = false;
        else isNumberValid = true;

        if (isEmailValid && isPasswordValid && isNumberValid) {
            return true;
        }
        else {
            Toast.makeText(getApplicationContext(), "Information entered invalid", Toast.LENGTH_SHORT).show();
            return false;
        }

    }
}