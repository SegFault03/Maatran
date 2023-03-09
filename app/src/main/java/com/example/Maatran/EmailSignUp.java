package com.example.Maatran;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.Maatran.utils.commonUIFunctions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//Called from RegistrationActivity
//email based sign-up
//xml file: screen-3
public class EmailSignUp extends AppCompatActivity implements commonUIFunctions {

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private boolean isPatient;
    String email, password, confirmPass, hospitalName="", employeeId="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConstraintLayout layout;
        mAuth = FirebaseAuth.getInstance();
        isPatient = getIntent().getBooleanExtra("isPatient", true);

        if(isPatient) {
            setContentView(R.layout.signup_as_patient);
            layout = findViewById(R.id.signup_as_patient_bg);
        }
        else {
            setContentView(R.layout.signup_as_worker);
            layout = findViewById(R.id.signup_as_worker_bg);
        }

        Drawable backgroundDrawable = layout.getBackground();
        changeStatusBarColor(backgroundDrawable, this);
        Button continue_btn = findViewById(R.id.continue_button);
        continue_btn.setOnClickListener(view -> {
            if(isPatient) {
                email = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.sign_in_as_patient_edit)).getText()).toString();
                password = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.sign_in_password_edit)).getText()).toString();
                confirmPass = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.sign_in_confirm_password_edit)).getText()).toString();
            }
            else
            {
                email = ((EditText) findViewById(R.id.sign_in_as_patient)).getText().toString();
                password = ((EditText) findViewById(R.id.password)).getText().toString();
                confirmPass = ((EditText) findViewById(R.id.confirm_password)).getText().toString();
                hospitalName = ((EditText) findViewById(R.id.hospital_name)).getText().toString();
                employeeId = ((EditText) findViewById(R.id.employee_id)).getText().toString();
            }
            if(password.length()>=6)
            {
                if(password.equals(confirmPass)) {
                    createAccount(email, password);
                }
                else
                    Toast.makeText(EmailSignUp.this, "Passwords don't match.",
                            Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(EmailSignUp.this, "Password must be at least 6 characters long",
                        Toast.LENGTH_SHORT).show();
        });
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(EmailSignUp.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                });
    }


    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    // Email sent
                });
        // [END send_email_verification]
    }

    public void signInOptions(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        super.finish();
    }


    private void updateUI(FirebaseUser user) {
        if(user != null)
        {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, String> details = new HashMap<>();
            if(!isPatient)
            {
                details.put("hospitalName", hospitalName);
                details.put("employeeId", employeeId);
                details.put("isWorker", "true");
                db.collection("UserDetails")
                        .document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()))
                        .set(details, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully written!"))
                        .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));
            }
            else
            {
                details.put("admin_id", "null");
                details.put("isWorker", "false");
                db.collection("UserDetails")
                        .document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()))
                        .set(details, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully written!"))
                        .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));
            }
            Intent intent = new Intent(getApplicationContext(), EditPatient.class);
            intent.putExtra("isPatient", true);
            intent.putExtra("newDetails", true);
            startActivity(intent);
            super.finish();
        }
    }
}