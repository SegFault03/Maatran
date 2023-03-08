package com.example.Maatran;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.invoke.ConstantCallSite;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements test{

    private static final String TAG = "MainActivity";
    int lastUpdatedDot = 0;
    Handler loadingDotHandler;
    Runnable loadingDotRunnable = this::changeLoadingDot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ConstraintLayout layout = findViewById(R.id.main_activity_bg);
        Drawable backgroundDrawable = layout.getBackground();
        change(backgroundDrawable,this);
        loadingDotHandler = new Handler();
        loadingDotHandler.postDelayed(loadingDotRunnable,300);
        signInOptions();
    }

    void changeLoadingDot()
    {
        String currLoadDotView = "load_dot"+String.valueOf(lastUpdatedDot%3);
        String oldLoadDotView = "load_dot"+String.valueOf(lastUpdatedDot%3==0?2:lastUpdatedDot%3-1);
        lastUpdatedDot++;
        int currResID = getResources().getIdentifier(currLoadDotView,"id",getPackageName());
        int oldResID = getResources().getIdentifier(oldLoadDotView,"id",getPackageName());
        AppCompatImageView currResView = findViewById(currResID);
        AppCompatImageView oldResView = findViewById(oldResID);
        currResView.setImageResource(R.drawable.loading_dot_green);
        oldResView.setImageResource(R.drawable.loading_dot_grey);
        loadingDotHandler.postDelayed(loadingDotRunnable,300);
    }

    public void signInOptions() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null) {
            Intent intent = new Intent(getApplicationContext(), RegisterSignUpActivity.class);
            startActivity(intent);
            super.finish();
        }
        else
        {
            checkForUserDetails(user);
        }
    }

    private void checkForUserDetails(FirebaseUser user)
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
                        super.finish();
                    }
                    else
                    {
                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(intent);
                        super.finish();
                    }
                }
                else
                    Log.d(TAG,"Document does not exist");
            }
            else
                Log.d(TAG,"Task failed to complete");
        });
    }
}