package com.example.Maatran;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.Maatran.utils.commonUIFunctions;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity implements commonUIFunctions {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        Drawable backgroundDrawable = AppCompatResources.getDrawable(this,R.drawable.concept_woman_thinking_laptop_forgot_your_password_account_login_505620_983);
        changeStatusBarColor(backgroundDrawable,this);
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