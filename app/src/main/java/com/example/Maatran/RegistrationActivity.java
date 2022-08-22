package com.example.Maatran;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_2);

    }

    public void signUp(View view) {
        Intent intent = new Intent(getApplicationContext(), EmailSignUp.class);
        startActivity(intent);
    }
}