package com.example.Maatran;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        String user_name = getIntent().getExtras().getString("UserName");
        setContentView(R.layout.change_password);
        TextView tv_name = findViewById(R.id.p_user_name);
        tv_name.setText(user_name);
    }

    public void changePassword(View view)
    {
        ProgressDialog progressDialog = new ProgressDialog(ChangePasswordActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating Password...");
        progressDialog.show();
        String new_password = ((EditText) findViewById(R.id.password_page_new_password)).getText().toString();
        String confirm_password = ((EditText) findViewById(R.id.password_page_confirm_password)).getText().toString();
        if(new_password.length()>=8)
        {
            if(new_password.equals(confirm_password))
            {
                FirebaseAuth.getInstance().getCurrentUser().updatePassword(new_password).addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        Log.d("ChangePasswordActivity", "Password updated");
                        progressDialog.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), "Password updated, logging out now", Toast.LENGTH_SHORT);
                        toast.show();
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Log.d("ChangePasswordActivity", "Error password not updated");
                        Toast toast = Toast.makeText(getApplicationContext(), "Error password not updated", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
            else
            {
                progressDialog.dismiss();
                Log.d("ChangePasswordActivity", "Password not matched");
                Toast toast = Toast.makeText(getApplicationContext(), "Password not matched", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else
        {
            progressDialog.dismiss();
            Toast toast = Toast.makeText(getApplicationContext(), "Password should be at least 8 characters", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void backToUserProfile(View view)
    {
        super.finish();
    }
}
