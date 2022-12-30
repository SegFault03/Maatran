package com.example.Maatran;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {
    public static final String TAG = "DashboardActivity";
    ProgressDialog progressDialog;
    FirebaseUser user;
    boolean isPatient = true;
    private static final int PERMISSION_SEND_SMS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_1);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data..");
        progressDialog.show();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        fetchUserDetails();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSOS();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    public void viewPatients(View view)
    {
        Intent intent = new Intent(getApplicationContext(), PatientsView.class);
        intent.putExtra("isPatient", isPatient);
        startActivity(intent);
    }

    public void userProfileView(View view)
    {
        Intent intent = new Intent(getApplicationContext(),ProfileView.class);
        startActivity(intent);
    }

    public void addPatient(View view)
    {
        Intent intent = new Intent(getApplicationContext(),EditPatient.class);
        intent.putExtra("isPatient", true);
        intent.putExtra("newDetails", true);
        startActivity(intent);
    }

    public void fetchUserDetails()
    {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        TextView user_name = findViewById(R.id.dashboard_user_name);
        DocumentReference df= db.collection("UserDetails").document(Objects.requireNonNull(user.getEmail()));
        df.get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                DocumentSnapshot ds = task.getResult();
                if(ds.exists()) {
                    user_name.setText(Objects.requireNonNull(ds.get("name")).toString());
                    if(Objects.requireNonNull(ds.get("isWorker")).toString().equals("true")) {
                        findViewById(R.id.report_button).setVisibility(View.GONE);
                        isPatient = false;
                    }
                }
                else
                    Log.d(TAG, "No such document");
            }
            else
                Log.d(TAG, "get failed with ", task.getException());
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        });
    }

    public void attemptSOS(View View)
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this,"Permission to send messages not available! Please grant it to continue!",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_SEND_SMS);
        }
        else
            sendSOS();
    }

    public void sendSOS()
    {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference df= db.collection("UserDetails").document(Objects.requireNonNull(user.getEmail()));
        CollectionReference ref = df.collection("Patients");
        SmsManager sms=SmsManager.getDefault();
        df.get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                DocumentSnapshot ds = task.getResult();
                if(ds.exists()) {
                    String number = Objects.requireNonNull(ds.get("mobile")).toString();
                    //TODO Why the f is there a 0 before the number??
                    sms.sendTextMessage(number, null, "I need help!\n-Message sent from: "+number+" from app: Maatran", null,null);
                }
                else
                    Log.d(TAG, "No such document");
            }
            else
                Log.d(TAG, "get failed with ", task.getException());
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        });
        ref.get().addOnSuccessListener(value -> {
            for (DocumentSnapshot dc : value.getDocuments()) {
                String number = Objects.requireNonNull(dc.get("mobile")).toString();
                sms.sendTextMessage(number, null, "I need help!\n-Message sent from"+number+" from app: Maatran", null,null);
            }
        });
        Toast.makeText(this,"sms sent successfully",Toast.LENGTH_SHORT).show();
    }

    public void bluetoothService(View view)
    {
        Intent intent = new Intent(getApplicationContext(),BluetoothActivity.class);
        startActivity(intent);
    }
}