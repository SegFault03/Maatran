package com.example.Maatran;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

//main Activity
//XML file: home_1
public class MainActivity extends AppCompatActivity {

private static final String TAG="MainActivity";
ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_1);
        progressDialog=new ProgressDialog(this);
        //Button test_btn = findViewById(R.id.test_btn);
        //test_btn.setOnClickListener(v-> signInWithTestAccount());
    }

    //for signing-in, calls LoginActivity.class
    public void signInOptions(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
        else
        {
            checkForUserDetails(user);
        }
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

    private void checkForUserDetails(FirebaseUser user)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait logging in....");
        progressDialog.show();
        db.collection("UserDetails").document(Objects.requireNonNull(user.getEmail())).get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                DocumentSnapshot document = task.getResult();
                if(document.exists())
                {
                    if(document.get("name")==null)
                    {
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(this, "Fill in your details to proceed...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), EditPatient.class);
                        intent.putExtra("isPatient",false);
                        intent.putExtra("newDetails", true);
                        startActivity(intent);
                    }
                    else
                    {
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(intent);
                    }
                }
                else
                    Log.d(TAG,"Document does not exist");
            }
            else
                Log.d(TAG,"Task failed to complete");
        });
    }

    private void signInWithTestAccount()
    {
        Intent blintent = new Intent(getApplicationContext(), BluetoothActivity.class);
        startActivity(blintent);
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

