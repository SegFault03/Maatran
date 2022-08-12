package com.example.Maatran;

<<<<<<<< HEAD:app/src/main/java/com/example/Maatran/SensorsHome.java
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SensorsHome extends AppCompatActivity {
========
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ReportsActivity extends AppCompatActivity {
>>>>>>>> beta:app/src/main/java/com/example/Maatran/ReportsActivity.java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensors_home);
    }

    public void backToHome(View view)
    {
        Intent intent = new Intent(getApplicationContext(),DashboardActivity.class);
        startActivity(intent);
    }

}