package com.example.Maatran;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.Maatran.utils.commonUIFunctions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity implements commonUIFunctions {
    public static final String TAG = "DashboardActivity";
    ProgressDialog progressDialog;
    FirebaseUser user;
    boolean isPatient = true;
    private static final int PERMISSION_SEND_SMS = 123;
    private static final int BLUETOOTH_SCAN = 5;
    private static final int BLUETOOTH_CONNECT = 6;
    ImageButton mProfilePic;
    private String and_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data..");
        progressDialog.show();
        mProfilePic = findViewById(R.id.dashboardProfilePic);
        mProfilePic.setImageResource(R.drawable.profile_ico_white);
        user = FirebaseAuth.getInstance().getCurrentUser();
        and_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Get a reference to your layout object
        ConstraintLayout layout = findViewById(R.id.dashboard_new_bg);

        // Get a reference to your layout's background drawable
        Drawable backgroundDrawable = layout.getBackground();
        changeStatusBarColor(backgroundDrawable,DashboardActivity.this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getPermissions();
        fetchUserDetails();
        new ModelApi(result -> Log.v(TAG,result)).execute(new ArrayList<>(Collections.singletonList("wakeup")));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
            case BLUETOOTH_CONNECT:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getApplicationContext(),
                            "Features using bluetooth will not work without granting this permission.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            case BLUETOOTH_SCAN:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getApplicationContext(),
                            "Features using bluetooth will not work without granting this permission.", Toast.LENGTH_LONG).show();
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
                    user_name.setText(Objects.requireNonNull("Hello "+ds.get("name")).toString()+"!");
                    if(Objects.requireNonNull(ds.get("isWorker")).toString().equals("true")) {
                        findViewById(R.id.rectangle_6).setVisibility(View.GONE);
                        findViewById(R.id.rectangle_3).setVisibility(View.GONE);
                        findViewById(R.id.rectangle_5).setVisibility(View.GONE);
                        findViewById(R.id.rectangle_8).setVisibility(View.GONE);
                        findViewById(R.id.imageView6).setVisibility(View.GONE);
                        findViewById(R.id.add_patient).setVisibility(View.GONE);
                        findViewById(R.id.imageView14).setVisibility(View.GONE);
                        findViewById(R.id.update_health).setVisibility(View.GONE);
                        TextView tv= findViewById(R.id.view_family);
                        tv.setText("VIEW PATIENTS");
                        isPatient = false;
                    }
//                    else
//                    {
//                        Button viewFamilybtn = findViewById(R.id.viewUsersBtn);
//                        Button addFamilybtn = findViewById(R.id.report_button);
//                        viewFamilybtn.setText("VIEW FAMILY");
//                        addFamilybtn.setText("ADD A FAMILY MEMBER");
//                    }
                    setUserProfile(ds);
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

    /**
     * Sets the user profile pic depending upon the age, gender and type of the user-profile.
     * Requires a global ImageView element which in this case is called {@link ProfileView#mProfilePic}
     * @param document: DocumentSnapshot of the document containing the data from which gender, age, etc.
     * from which the profile pic will be inferred.
     */
    public void setUserProfile(DocumentSnapshot document) {
        String type,gen = Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("gender")).toString().trim();
        type = Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("isWorker")).toString().equals("false")?"p":"h";
        gen = gen.equalsIgnoreCase("male") ?"m":(gen.equalsIgnoreCase("female")?"w":"o");

        if(!type.equals("h")) {
            int age = Integer.parseInt(Objects.requireNonNull(document.getData().get("age")).toString());
            if(!gen.equals("o"))
                gen = gen.equals("m")?(age>20?"m":"b"):(age>20?"w":"g");
        }
        String uri = gen.equals("o")?"@drawable/profile_ico_white":"@drawable/"+type+"_"+gen;
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        Drawable res = getResources().getDrawable(imageResource);
        mProfilePic.setImageDrawable(res);
    }

    public boolean checkForPermissions()
    {
        if (ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_DENIED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            {
                Log.d(TAG,"Bl scan location permission not available");
                ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, BLUETOOTH_SCAN);
            }
            if (ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_DENIED)
                return false;
        }
        if (ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            {
                ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_CONNECT);
            }
            if (ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_DENIED)
                return false;
        }
        return true;
    }

    public void bluetoothService(View view)
    {
        getPermissions();
        if(checkForPermissions()) {
            Intent intent = new Intent(getApplicationContext(), BluetoothActivity.class);
            intent.putExtra("and_id", and_id);
            startActivity(intent);
        }
        else
            Toast.makeText(this,"This feature can't work without Bluetooth permissions",Toast.LENGTH_SHORT).show();
    }

    public void getPermissions(){
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    //TODO This is currently being used for debugging purposes. Should be removed before a release.
    public void callModel(View view)
    {
        ModelApi.ModelApiCallback modelApiCallback = result -> {
            Log.v(TAG,result);
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
        };
        ModelApi mApi = new ModelApi(modelApiCallback);
        ArrayList<String> dataPackets = new ArrayList<>();
        dataPackets.add(String.valueOf(16));
        dataPackets.add(String.valueOf(100));
        dataPackets.add(String.valueOf(70));
        dataPackets.add(String.valueOf(7.2));
        dataPackets.add(String.valueOf(98));
        dataPackets.add(String.valueOf(80));
        dataPackets.add("predict");
        new ModelApi(modelApiCallback).execute(dataPackets);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}