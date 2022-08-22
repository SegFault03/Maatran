package com.example.Maatran;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

//login activity, called from main activity
//corresponding xml file: screen_1
public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_1);
        //getSupportActionBar().hide();
    }

    //called when SignIn button is clicked, calls EmailPasswordActivity
    public void signIn(View view) {
        Intent intent = new Intent(getApplicationContext(), EmailPasswordActivity.class);
        startActivity(intent);
    }


}
