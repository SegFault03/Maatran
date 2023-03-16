package com.example.Maatran;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.Maatran.utils.commonUIFunctions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ProfileView extends AppCompatActivity implements commonUIFunctions {
    FirebaseFirestore db;
    FirebaseUser user;
    boolean isWorker;
    public static final String TAG="ProfileView";
    ProgressDialog progressDialog;
    ImageView mProfilePic;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        ConstraintLayout layout = findViewById(R.id.user_profile_bg);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data..");
        progressDialog.show();
        user = FirebaseAuth.getInstance().getCurrentUser();
        TextView email = findViewById(R.id.emailId);
        email.setText(user.getEmail());
        url = "@drawable/profile_ico_white";
        mProfilePic = (ImageView)findViewById(R.id.user_profile_pic);
        mProfilePic.setImageResource(R.drawable.profile_ico_white);
        Drawable backgroundDrawable = layout.getBackground();
        changeStatusBarColor(backgroundDrawable,ProfileView.this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getUserDetails(user);
    }

    public void getUserDetails(FirebaseUser user)
    {

        db = FirebaseFirestore.getInstance();
        isWorker = false;
        DocumentReference docRef = db.collection("UserDetails").document(Objects.requireNonNull(user.getEmail()));
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    if(Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("isWorker")).toString().equals("false")) {
                        findViewById(R.id.hospital_details).setVisibility(View.GONE);
                        findViewById(R.id.view_l7).setVisibility(View.GONE);
                        findViewById(R.id.employee_details).setVisibility(View.GONE);
                        findViewById(R.id.view_l8).setVisibility(View.GONE);
                        findViewById(R.id.age_details).setVisibility(View.GONE);
                        findViewById(R.id.view_l5).setVisibility(View.GONE);
                    }
                    else
                    {
                        isWorker=true;
                        findViewById(R.id.age_details).setVisibility(View.GONE);
                        findViewById(R.id.view_l5).setVisibility(View.GONE);
                        TextView tv_hosp = findViewById(R.id.hospital_name);
                        tv_hosp.setText(Objects.requireNonNull(document.getData().get("hospitalName")).toString());
                        TextView tv_eid = findViewById(R.id.employee_id);
                        tv_eid.setText(Objects.requireNonNull(document.getData().get("employeeId")).toString());
                    }
                    TextView tv_name = findViewById(R.id.user_name);
                    tv_name.setText(Objects.requireNonNull(document.getData().get("name")).toString());
                    TextView tv_gender = findViewById(R.id.user_gender);
                    tv_gender.setText(Objects.requireNonNull(document.getData().get("gender")).toString());
                    TextView tv_adr = findViewById(R.id.user_adr);
                    tv_adr.setText(Objects.requireNonNull(document.getData().get("address")).toString());
                    TextView tv_mob = findViewById(R.id.user_mob);
                    tv_mob.setText(Objects.requireNonNull(document.getData().get("mobile")).toString());
                    setUserProfile(document);
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
        db.collection("UserDetails")
                .document(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())).get().addOnSuccessListener(documentSnapshot -> {
                    Intent intent = new Intent(getApplicationContext(), EditPatient.class);
                    intent.putExtra("isPatient", true);
                    intent.putExtra("user", documentSnapshot.toObject(User.class));
                    startActivity(intent);
                });
    }

    public void signOut(View view)
    {
        View popupConfirmSignOut = getLayoutInflater().inflate(R.layout.popupview_confirmation,null);
        ((TextView) popupConfirmSignOut.findViewById(R.id.text_dialog)).setText("Do you really want to sign out?");
        PopupWindow popupWindow = new PopupWindow(popupConfirmSignOut,LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,true);
        popupWindow.showAtLocation(view,Gravity.CENTER,0,0);
        popupConfirmSignOut.findViewById(R.id.btn_yes).setOnClickListener(v->{
            FirebaseAuth.getInstance().signOut();
            Toast toast = Toast.makeText(getApplicationContext(),"You have successfully signed out, redirecting you to the log-in page",Toast.LENGTH_SHORT);
            toast.show();
            Intent intent = new Intent(getApplicationContext(), RegisterSignUpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            super.finish();
        });
        popupConfirmSignOut.findViewById(R.id.btn_no).setOnClickListener(v->popupWindow.dismiss());
    }

    public void deleteProfile(View view)
    {
        LayoutInflater inflater = getLayoutInflater();
        View popupDeleteProfile = inflater.inflate(R.layout.popupview_confirmation,null);
        ((TextView)popupDeleteProfile.findViewById(R.id.text_dialog)).setText("Do you really want to delete your profile?");
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
        intent.putExtra("ResLoc",url);
        startActivity(intent);
    }

    public void deleteUserProfile(ProgressDialog progressDialog,PopupWindow popupWindow)
    {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        user.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                deleteUserData(user);
                Log.d(TAG, "User account deleted.");
                Toast toast = Toast.makeText(getApplicationContext(), "User account deleted.", Toast.LENGTH_SHORT);
                toast.show();
                progressDialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("UserDetails").document(Objects.requireNonNull(user.getEmail()));
        if(!isWorker) {
            docRef.get().addOnSuccessListener(value -> {
                if(Objects.requireNonNull(value.getData()).get("admin_id").toString().equals("null")){}
                else if(Objects.requireNonNull(value.getData()).get("admin_id").toString().equals(user.getEmail()))
                {
                    docRef.collection("Patients").get()
                            .addOnSuccessListener(task -> {
                                for(DocumentSnapshot ds: task.getDocuments())
                                {
                                    ds.getReference().delete()
                                            .addOnSuccessListener(aVoid -> Log.d("TAG", "Sub-document deleted!"))
                                            .addOnFailureListener(e -> Log.w("TAG", "Error deleting sub-document", e));
                                }
                            });
                }
                else {
                    db.collection("UserDetails").document(Objects.requireNonNull(value.getData()).get("admin_id").toString())
                            .collection("Patients")
                            .document(user.getEmail()).delete()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                } else {
                                    Log.d(TAG, "Error deleting document", task.getException());
                                }
                            });
                }
            });
        }

        docRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "DocumentSnapshot successfully deleted!");
            } else {
                Log.d(TAG, "Error deleting document", task.getException());
            }
        });
    }
    /**
     * Sets the user profile pic depending upon the age, gender and type of the user-profile.
     * Requires a global ImageView element which in this case is called {@link ProfileView#mProfilePic}
     * @param document: DocumentSnapshot of the document containing the data from which gender, age, etc.
     * from which the profile pic will be inferred.
    */
    public void setUserProfile(DocumentSnapshot document) {
        String type,gen = Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("gender")).toString().trim();
        type=isWorker?"h":"p";
        gen = gen.equalsIgnoreCase("male") ?"m":(gen.equalsIgnoreCase("female")?"w":"o");

        if(!isWorker) {
            int age = Integer.parseInt(Objects.requireNonNull(document.getData().get("age")).toString());
            if(!gen.equals("o"))
                gen = gen.equals("m")?(age>20?"m":"b"):(age>20?"w":"g");
        }
        String uri = gen.equals("o")?"@drawable/profile_ico_white":"@drawable/"+type+"_"+gen;
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        Drawable res = getResources().getDrawable(imageResource);
        mProfilePic.setImageDrawable(res);
        url = uri;
    }
}