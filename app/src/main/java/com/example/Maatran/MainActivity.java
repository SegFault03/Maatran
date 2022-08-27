package com.example.Maatran;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

//main Activity
//XML file: home_1
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_1);

    }

    //for signing-in, calls LoginActivity.class
    public void signInOptions(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    //for registration, calls RegistrationActivity.class
    public void registerOptions(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View popupRegister = inflater.inflate(R.layout.register_options,null);
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        // lets taps outside the popupWindow dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupRegister, width, height, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        Button patient = popupRegister.findViewById(R.id.patient);
        Button worker = popupRegister.findViewById(R.id.worker);
        patient.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EmailSignUp.class);
            intent.putExtra("isPatient", true);
            startActivity(intent);
        });
        worker.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EmailSignUp.class);
            intent.putExtra("isPatient", false);
            startActivity(intent);
        });
    }
}

