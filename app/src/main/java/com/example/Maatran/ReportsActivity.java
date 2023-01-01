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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_sensor_information);

        user = getIntent().getParcelableExtra("user");
        userId = getIntent().getStringExtra("id");

        DocumentReference df = FirebaseFirestore.getInstance().collection("UserDetails")
                .document(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()));
        df.get().addOnCompleteListener(task->{
            DocumentSnapshot ds = task.getResult();
            if(ds.get("isWorker").equals("true"))
            {
                findViewById(R.id.edit_patient).setVisibility(View.INVISIBLE);
                findViewById(R.id.delete_patient).setVisibility(View.INVISIBLE);
            }
        });

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
    public void editPatient(View view)
    {
        Intent intent = new Intent(getApplicationContext(), EditPatient.class);
        intent.putExtra("user", user);
        intent.putExtra("id", userId);
        startActivityForResult(intent, 0);
    }

    public void deletePatient(View view)
    {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        assert mUser != null;
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection("UserDetails")
                .document(Objects.requireNonNull(mUser.getEmail()))
                .collection("Patients")
                .document(userId);
        docRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("ReportsActivity", "DocumentSnapshot successfully deleted!");
                Toast toast = Toast.makeText(getApplicationContext(),"Patient data has been deleted",Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Log.d("ReportsActivity", "Error deleting document", task.getException());
            }
        });
        super.finish();
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