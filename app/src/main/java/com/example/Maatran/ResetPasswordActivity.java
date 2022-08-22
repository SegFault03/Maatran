package com.example.Maatran;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

    }

    public void resetPassword(View view)
    {
        String email_id = ((EditText) findViewById(R.id.forgot_password_mail_id)).getText().toString();
        if(email_id.isEmpty())
        {
            Toast toast = Toast.makeText(getApplicationContext(), "Please enter your email id", Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.sendPasswordResetEmail(email_id).addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Password reset link sent to your email id", Toast.LENGTH_SHORT);
                    toast.show();
                    super.finish();
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Error in sending password reset link", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }

    public void backToLogin(View view)
    {
        super.finish();
    }
}