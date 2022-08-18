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
import android.widget.TextView;
import android.widget.Toast;

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

    public void deleteProfile(View view)
    {
        LayoutInflater inflater = getLayoutInflater();
        View popupDeleteProfile = inflater.inflate(R.layout.popupview_confirmation,null);
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        // lets taps outside the popupWindow dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupDeleteProfile, width, height, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        Button yes = popupDeleteProfile.findViewById(R.id.btn_yes);
        Button no = popupDeleteProfile.findViewById(R.id.btn_no);
        yes.setOnClickListener(v -> {
            ProgressDialog progressDialog = new ProgressDialog(ProfileView.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Deleting profile..");
            progressDialog.show();
            deleteUserProfile(progressDialog,popupWindow);
        });
        no.setOnClickListener(v -> popupWindow.dismiss());
    }

    public void backToDashboard(View view)
    {
        super.finish();
    }

    public void changePassword(View view)
    {
        Intent intent = new Intent(getApplicationContext(),ChangePasswordActivity.class);
        intent.putExtra("UserName",((TextView) findViewById(R.id.user_name)).getText().toString());
        startActivity(intent);
    }

    public void deleteUserProfile(ProgressDialog progressDialog,PopupWindow popupWindow)
    {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        user.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                deleteUserData(user);
                Log.d(TAG, "User account deleted.");
                Toast toast = Toast.makeText(getApplicationContext(), "User account deleted.", Toast.LENGTH_SHORT);
                toast.show();
                progressDialog.dismiss();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Error deleting user account.", Toast.LENGTH_SHORT);
                toast.show();
                progressDialog.dismiss();
                popupWindow.dismiss();
                Log.d(TAG, "Error deleting document", task.getException());
            }
        });
    }

    public void deleteUserData(FirebaseUser user)
    {
        db=FirebaseFirestore.getInstance();
        DocumentReference docRef=db.collection("UserDetails").document(user.getEmail());
        docRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "DocumentSnapshot successfully deleted!");
            } else {
                Log.d(TAG, "Error deleting document", task.getException());
            }
        });
    }
}