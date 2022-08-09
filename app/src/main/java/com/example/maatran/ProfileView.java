package com.example.maatran;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileView extends AppCompatActivity {

    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        getSupportActionBar().hide();

    }

    public void backToHome(View view)
    {
        Intent intent = new Intent(getApplicationContext(),DashboardActivity.class);
        startActivity(intent);
    }

}