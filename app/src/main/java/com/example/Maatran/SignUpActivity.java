package com.example.Maatran;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class SignUpActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    MaterialButton signUpBtn;
    TextView signInOption;
    LinearLayout loadingAnimation;
    int tabSelected;
    private final static String TAG = "SignUpActivity";
    String email, password, confirmPass, hospitalName, employeeId;
    FirebaseAuth mAuth;
    int lastUpdatedDot = 0;
    Handler loadingDotHandler;
    Runnable loadingDotRunnable = this::changeLoadingDot;
    SignUpFragment signUpFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);
        fragmentManager = getSupportFragmentManager();
        tabSelected = 0;
        signUpBtn = findViewById(R.id.signup_btn);
        signInOption = findViewById(R.id.signup_signin_options);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        findViewById(R.id.backBtn).setOnClickListener(v->super.finish());
        loadingAnimation = findViewById(R.id.loadingAnimation);
        loadingAnimation.setVisibility(View.GONE);
        loadingDotHandler = new Handler();
        loadingDotHandler.postDelayed(loadingDotRunnable,300);
        if (savedInstanceState == null) {
            signUpFragment = SignUpFragment.newInstance(false);
            fragmentManager.beginTransaction().add(R.id.signupFragmentContainer, signUpFragment)
                    .setReorderingAllowed(true)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tabSelected = 0;
                        signUpFragment = SignUpFragment.newInstance(false);
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(
                                        R.anim.slide_in,  // enter
                                        R.anim.fade_out  // exit
                                )
                                .replace(R.id.signupFragmentContainer, signUpFragment)
                                .setReorderingAllowed(true)
                                .commit();
                        break;

                    case 1:
                        tabSelected = 1;
                        signUpFragment = SignUpFragment.newInstance(true);
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(
                                        R.anim.slide_in,  // enter
                                        R.anim.fade_out  // exit
                                )
                                .replace(R.id.signupFragmentContainer, signUpFragment)
                                .setReorderingAllowed(true)
                                .commit();
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        signUpBtn.setOnTouchListener((v, event) -> {
            if(event.getAction()==MotionEvent.ACTION_DOWN)
            {
                v.setBackgroundColor(getResources().getColor(R.color.white));
                changeTextColor(1);
                return true;
            }
            if(event.getAction()==MotionEvent.ACTION_UP)
            {
                v.performClick();
                v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                changeTextColor(0);
                return true;
            }
            return false;
        });
        signUpBtn.setOnClickListener(v->signIn());
        signInOption.setOnClickListener(view->signInOptions());
    }

    public void changeTextColor(int type)
    {
        if(type==1)
            signUpBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
        else
            signUpBtn.setTextColor(getResources().getColor(R.color.white));
    }

    void changeLoadingDot()
    {
        String currLoadDotView = "load_dot"+ lastUpdatedDot % 3;
        String oldLoadDotView = "load_dot"+ (lastUpdatedDot % 3 == 0 ? 2 : lastUpdatedDot % 3 - 1);
        lastUpdatedDot++;
        int currResID = getResources().getIdentifier(currLoadDotView,"id",getPackageName());
        int oldResID = getResources().getIdentifier(oldLoadDotView,"id",getPackageName());
        AppCompatImageView currResView = findViewById(currResID);
        AppCompatImageView oldResView = findViewById(oldResID);
        Drawable drawable = AppCompatResources.getDrawable(this,R.drawable.loading_dot_grey);
        drawable.setColorFilter(ContextCompat.getColor(this,R.color.login_accent), PorterDuff.Mode.SRC_IN);
        currResView.setImageDrawable(drawable);
        oldResView.setImageResource(R.drawable.loading_dot_grey);
        loadingDotHandler.postDelayed(loadingDotRunnable,300);
    }

    public void signIn() {

        View fragmentView = signUpFragment.getView();
        email = (((TextInputEditText) fragmentView.findViewById(R.id.signup_email_edit)).getText()).toString();
        password = (((TextInputEditText) fragmentView.findViewById(R.id.signup_pwd_edit)).getText()).toString();
        confirmPass = (((TextInputEditText) fragmentView.findViewById(R.id.signup_pwd_confirm_edit)).getText()).toString();
        if (tabSelected == 1) {
            hospitalName = ((EditText) fragmentView.findViewById(R.id.signup_hospital_edit)).getText().toString();
            employeeId = ((EditText) fragmentView.findViewById(R.id.signup_empid_edit)).getText().toString();
            if(hospitalName.length()==0)
            {
                Toast.makeText(this, "Hospital name field is empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(employeeId.length()==0)
            {
                Toast.makeText(this, "EmployeeID field is empty!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (email.length() == 0) {
            Toast.makeText(this, "Email field is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password length must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPass)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO perform Hospital Name and EmployeeID validation
        createAccount();
    }

    private void createAccount() {
        loadingAnimation.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
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
                        Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }


    public void signInOptions() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }


    private void updateUI(FirebaseUser user) {
        loadingAnimation.setVisibility(View.GONE);
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, String> details = new HashMap<>();
            if (tabSelected==1) {
                details.put("hospitalName", hospitalName);
                details.put("employeeId", employeeId);
                details.put("isWorker", "true");
                db.collection("UserDetails")
                        .document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()))
                        .set(details, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully written!"))
                        .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));
            } else {
                details.put("isWorker", "false");
                db.collection("UserDetails")
                        .document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()))
                        .set(details, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully written!"))
                        .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));
            }
            Intent intent = new Intent(getApplicationContext(), EditPatient.class);
            intent.putExtra("isPatient", false);
            intent.putExtra("newDetails", true);
            startActivity(intent);
            super.finish();
        }
    }
}