package com.example.maatran;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.os.Bundle;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    ImageButton login;
    public static int user=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_1);
    }

    public void signInOptions(View view) {
        setContentView(R.layout.screen_1);
    }

    public void registerOptions(View view) {
        Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
        startActivity(intent);
    }

    public void signIn(View view) {
        setContentView(R.layout.screen_5);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        ImageButton login = (ImageButton) findViewById(R.id.login_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetValidation();
            }
        });

    }

    public void SetValidation() {
        boolean isEmailValid, isPasswordValid;
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
        } else  {
            isPasswordValid = true;
        }

        if (isEmailValid && isPasswordValid) {
            Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Email address or Password invalid", Toast.LENGTH_SHORT).show();
        }

    }

    public void toggleButton(View view) {
        ImageButton btn1 = (ImageButton) findViewById(R.id.patient_btn);
        ImageButton btn2 = (ImageButton) findViewById(R.id.hw_btn);
        if(view.getId()==R.id.patient_btn) {
            btn1.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectangle_14_shape));
            btn2.setBackgroundDrawable(getResources().getDrawable(R.drawable._rectangle_12_shape));
            user=1;
        }
        else {
            btn2.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectangle_14_shape));
            btn1.setBackgroundDrawable(getResources().getDrawable(R.drawable._rectangle_12_shape));
            user=0;
        }
    }

}
