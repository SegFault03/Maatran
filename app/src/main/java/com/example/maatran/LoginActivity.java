package com.example.maatran;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_1);
    }

    public void signIn(View view) {
       // setContentView(R.layout.screen_5);
        Intent intent = new Intent(getApplicationContext(), EmailPasswordActivity.class);
        startActivity(intent);
    }


}
