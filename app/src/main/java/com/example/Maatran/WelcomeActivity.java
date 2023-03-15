package com.example.Maatran;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.Maatran.utils.UIFunctions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//main Activity
//XML file: home_1
public class WelcomeActivity extends AppCompatActivity implements UIFunctions {

private static final String TAG="WelcomeActivity";
ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);
        ConstraintLayout layout = findViewById(R.id.registrationLogin_bg);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        progressDialog=new ProgressDialog(this);
        Button test_btn = findViewById(R.id.test_btn);
        test_btn.setVisibility(View.GONE);
        test_btn.setOnClickListener(v-> signInWithTestAccount());
    }

    //for signing-in, calls LoginActivity.class
    public void signInOptions(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    //for registration, calls RegistrationActivity.class
    public void registerOptions(View view) {
        Intent intent  = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }

    private void signInWithTestAccount()
    {
        Intent intent  = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
//        String email = "testpatient4@gmail.com";
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

