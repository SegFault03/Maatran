package com.example.Maatran;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ReportsActivity extends AppCompatActivity {
    User user;
    String userId;
    DocumentReference df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_sensor_information);

        user = getIntent().getParcelableExtra("user");
        userId = getIntent().getStringExtra("id");

        df = FirebaseFirestore.getInstance().collection("UserDetails")
                .document(Objects.requireNonNull(user.getEmail()));

    }

    //TODO This does not dynamically render details once they are changed.
    @Override
    public void onResume()
    {
        super.onResume();
        TextView name = findViewById(R.id.patient_name);
        name.setText("Name: "+user.getName());
        TextView age = findViewById(R.id.patient_age);
        age.setText("Age: "+user.getAge());
        TextView sex = findViewById(R.id.patient_sex);
        sex.setText("Sex: "+user.getGender());
        TextView address = findViewById(R.id.patient_add);
        address.setText("Address: "+user.getAddress());
        TextView locality = findViewById(R.id.locality);
        locality.setText("Locality: "+user.getLocality());
        TextView no = findViewById(R.id.patient_no);
        no.setText("Emergency no: "+user.getEmergency());
        ImageView profilePic = findViewById(R.id.patientReportProfilePic);
        profilePic.setImageDrawable(setProfilePic(user, user.getGender(), (int) user.getAge()));
        setSensorInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            if (data.hasExtra("user")) {
                user = data.getExtras().getParcelable("user");
                onResume();
            }
        }
    }

    public void setSensorInfo()
    {
        TextView risk = findViewById(R.id.risk);
        TextView spo2 = findViewById(R.id.sp02);
        TextView temp = findViewById(R.id.temp);
        TextView pulse = findViewById(R.id.pr);
        TextView sp = findViewById(R.id.sp);
        TextView dp = findViewById(R.id.dp);
        TextView meta = findViewById(R.id.meta);

        df.get().addOnSuccessListener(task ->{
            if(task.contains("risk"))
            risk.setText(task.getData().get("risk").toString());
            if(task.contains("1"))
            spo2.setText(task.getData().get("1").toString());
            if(task.contains("2"))
            temp.setText(task.getData().get("2").toString());
            if(task.contains("3"))
            pulse.setText(task.getData().get("3").toString());
            if(task.contains("4"))
            sp.setText(task.getData().get("4").toString());
            if(task.contains("5"))
            dp.setText(task.getData().get("5").toString());
            if(task.contains("6"))
            meta.setText(task.getData().get("6").toString());
        });
    }

    public Drawable setProfilePic(User user, String lgen, int age)
    {
        String gen = lgen.equalsIgnoreCase("male") ?"m":(lgen.equalsIgnoreCase("female")?"w":"o");
        if(!gen.equals("o"))
            gen = gen.equals("m")?(age>20?"m":"b"):(age>20?"w":"g");
        String uri = gen.equals("o")?"@drawable/profile_ico_white":"@drawable/"+"p"+"_"+gen;
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        Drawable res = getResources().getDrawable(imageResource);
        return res;
    }

    public void backToHome(View view)
    {
        super.finish();
    }

}