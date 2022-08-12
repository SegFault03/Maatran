package com.example.Maatran;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        getUserDetails(user);
        setContentView(R.layout.user_profile);
    }

    public void backToHome(View view)
    {
        Intent intent = new Intent(getApplicationContext(),DashboardActivity.class);
        startActivity(intent);
    }

    public void getUserDetails(FirebaseUser user)
    {
        db=FirebaseFirestore.getInstance();
        DocumentReference docRef=db.collection("UserDetails").document(user.getEmail());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    TextView tv_name= findViewById(R.id.user_name);
                    tv_name.setText(document.getData().get("name").toString());
                    TextView tv_age= findViewById(R.id.user_age);
                    tv_age.setText(document.getData().get("age").toString());
                    TextView tv_gender= findViewById(R.id.user_gender);
                    tv_gender.setText(document.getData().get("gender").toString());
                    TextView tv_adr= findViewById(R.id.user_adr);
                    tv_adr.setText(document.getData().get("address").toString());
                    TextView tv_mob= findViewById(R.id.user_mob);
                    tv_mob.setText(document.getData().get("mobile").toString());
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData().get("address"));
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    public void editProfile(View view)
    {
        Intent intent = new Intent(getApplicationContext(),DetailsActivity.class);
        startActivity(intent);
    }
    public void signOut(View view)
    {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }
}