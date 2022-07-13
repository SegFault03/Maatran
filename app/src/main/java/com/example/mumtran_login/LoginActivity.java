package com.example.mumtran_login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_1);
    }

    public void signInOptions(View view) {
        setContentView(R.layout.screen_1);
    }

    public void registerOptions(View view) {
        setContentView(R.layout.screen_2);
    }

    public void signIn(View view) {
        setContentView(R.layout.screen_5);
    }

    public void signUpAsPatient(View view) {
        setContentView(R.layout.screen_4);/*
        EditText name = (EditText)findViewById(R.id.name);
        EditText age = (EditText)findViewById(R.id.age);
        EditText gender = (EditText)findViewById(R.id.gender);
        EditText no = (EditText)findViewById(R.id.mobile_number);
        EditText email = (EditText)findViewById(R.id.email);
        EditText address = (EditText)findViewById(R.id.address);
        ImageButton create = (ImageButton) findViewById(R.id.create_account);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
    }

    public void signUpAsHW(View view) {
        setContentView(R.layout.screen_3);
    }

    public void toggleButton(View view) {
        ImageButton btn1 = (ImageButton) findViewById(R.id.patient_btn);
        ImageButton btn2 = (ImageButton) findViewById(R.id.hw_btn);
        if(view.getId()==R.id.patient_btn) {
            btn1.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectangle_14_shape));
            btn2.setBackgroundDrawable(getResources().getDrawable(R.drawable._rectangle_12_shape));
        }
        else {
            btn2.setBackgroundDrawable(getResources().getDrawable(R.drawable.rectangle_14_shape));
            btn1.setBackgroundDrawable(getResources().getDrawable(R.drawable._rectangle_12_shape));
        }
    }

}
