package com.example.Maatran;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileView extends AppCompatActivity {
    FirebaseFirestore db;
    public static final String TAG="ProfileView";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        getSupportActionBar().hide();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data..");
        progressDialog.show();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        getUserDetails(user);
    }


    public void getUserDetails(FirebaseUser user)
    {

        db=FirebaseFirestore.getInstance();
        DocumentReference docRef=db.collection("UserDetails").document(user.getEmail());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    TextView tv_name = findViewById(R.id.user_name);
                    tv_name.setText(document.getData().get("user_name").toString());
                }
                else
                {
                    Log.d(TAG, "No such document");
                }
            }
            else
            {
                Log.d(TAG, "get failed with ", task.getException());
            }
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        });

        TextView tv_email = findViewById(R.id.user_email);
        tv_email.setText(user.getEmail());

        /*CollectionReference ref = db.collection("UserDetails").document(user.getEmail()).collection("Patients");
        ref.get().addOnSuccessListener(value -> {
            int users=0;
            for(DocumentSnapshot dc : value.getDocuments())
            {
                ++users;
            }
            TextView tv_no = findViewById(R.id.user_no);
            tv_no.setText(users);
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        });*/
    }

    public void editProfile(View view)
    {
        Intent intent = new Intent(getApplicationContext(),DetailsActivity.class);
        intent.putExtra("isPatient",false);
        startActivity(intent);
    }

    public void signOut(View view)
    {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    public void backToDashboard(View view)
    {
        super.finish();
    }
}