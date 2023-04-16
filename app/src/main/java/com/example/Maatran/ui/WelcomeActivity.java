package com.example.Maatran.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.Maatran.R;
import com.example.Maatran.tests.AppNavigationActivity;
import com.google.android.material.button.MaterialButton;

//main Activity
//XML file: home_1
public class WelcomeActivity extends AppCompatActivity {

private static final String TAG="WelcomeActivity";
MaterialButton signInBtn;
MaterialButton signUpBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        getWindow().setStatusBarColor(getResources().getColor(R.color.welcome_accent));
        Button test_btn = findViewById(R.id.test_btn);
        test_btn.setVisibility(View.INVISIBLE);
        test_btn.setOnClickListener(v-> signInWithTestAccount());
        signInBtn = findViewById(R.id.welcome_signin_btn);
        signUpBtn = findViewById(R.id.welcome_register_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        signInBtn.setOnClickListener(v->signInOptions());
        signInBtn.setOnTouchListener((v, event) -> {
            if(event.getAction()==MotionEvent.ACTION_DOWN)
            {
                v.setBackground(AppCompatResources.getDrawable(this,R.drawable.welcome_button_clickedstate));
                changeTextColor(1,signInBtn);
                return true;
            }
            if(event.getAction()==MotionEvent.ACTION_UP)
            {
                v.performClick();
                v.setBackground(AppCompatResources.getDrawable(this,R.drawable.welcome_button_nonclickedstate));
                changeTextColor(0,signInBtn);
                return true;
            }
            return false;
        });
        signUpBtn.setOnClickListener(v->registerOptions());
        signUpBtn.setOnTouchListener((v, event) -> {
            if(event.getAction()==MotionEvent.ACTION_DOWN)
            {
                v.setBackground(AppCompatResources.getDrawable(this,R.drawable.welcome_button_clickedstate));
                changeTextColor(1,signUpBtn);
                return true;
            }
            if(event.getAction()==MotionEvent.ACTION_UP)
            {
                v.performClick();
                v.setBackground(AppCompatResources.getDrawable(this,R.drawable.welcome_button_nonclickedstate));
                changeTextColor(0,signUpBtn);
                return true;
            }
            return false;
        });
    }

    public void changeTextColor(int type, Button btn)
    {
        if(type==1)
            btn.setTextColor(getResources().getColor(R.color.white));
        else
            btn.setTextColor(getResources().getColor(R.color.welcome_accent));
    }

    //for signing-in, calls LoginActivity.class
    public void signInOptions() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    //for registration, calls RegistrationActivity.class
    public void registerOptions() {
        Intent intent  = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }

    private void signInWithTestAccount()
    {
          Intent intent = new Intent(this, AppNavigationActivity.class);
          startActivity(intent);
//        String email = "test1@gmail.com";
//        String password = "123456";
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        auth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        // Sign in success, update UI with the signed-in user's information
//                        Log.d(TAG, "signInWithEmail:success");
//                        Toast.makeText(this,"WARNING: USE THIS FEATURE FOR DEBUG/TEST PURPOSES ONLY",Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
//                        startActivity(intent);
//                    } else {
//                        // If sign in fails, display a message to the user.
//                        Log.w(TAG, "signInWithEmail:failure", task.getException());
//                        Toast.makeText(this, "Authentication failed.",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
    }

}

