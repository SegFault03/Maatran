package com.example.Maatran;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

//login activity, called from main activity
//corresponding xml file: screen_1
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.login_signup);
        Button login = findViewById(R.id.continue_button);
        login.setOnClickListener(view -> {
            String email =  Objects.requireNonNull(((TextInputEditText) findViewById(R.id.sign_in_edit)).getText()).toString();
            String password = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.sign_in_password_edit)).getText()).toString();
            signIn(email, password);
        });
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            checkForDetails(currentUser);
        }
    }
    // [END on_start_check_user]


    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        boolean flag=true;
        if(email.length()==0) {
            Toast.makeText(this, "Email field is empty", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if(password.length()==0) {
            Toast.makeText(this, "Password field is empty", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if(flag) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            checkForDetails(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    });
        }
        // [END sign_in_with_email]
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

    private void updateUI(FirebaseUser user)
    {
        if(user!=null)
        {
            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(intent);
        }
    }

    /**Checks if user-details are available
    @params FirebaseUser user
     */
    private void checkForDetails(FirebaseUser user)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
         db.collection("UserDetails").document(Objects.requireNonNull(user.getEmail())).get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                DocumentSnapshot document = task.getResult();
                if(document.exists())
                {
                    if(document.get("name")==null)
                    {
                        Toast.makeText(this, "Fill in your details to proceed...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), EditPatient.class);
                        intent.putExtra("isPatient",false);
                        intent.putExtra("newDetails", true);
                        startActivity(intent);
                    }
                    else
                    {
                        updateUI(user);
                    }
                }
                else
                    Log.d(TAG,"Document does not exist");
            }
            else
                Log.d(TAG,"Task failed to complete");
         });
    }

    public void resetPassword(View view)
    {
        Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
        startActivity(intent);
    }

    public void signUpAsPatient(View view)
    {
        Intent intent = new Intent(getApplicationContext(), EmailSignUp.class);
        intent.putExtra("isPatient", true);
        startActivity(intent);
    }

    public void signUpAsWorker(View view)
    {
        Intent intent = new Intent(getApplicationContext(), EmailSignUp.class);
        intent.putExtra("isPatient", false);
        startActivity(intent);
    }
}
