package com.example.Maatran;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

//Activity that initializes the RecyclerView
//The interface onPatientListener is defined in UserAdapter.class
public class PatientsView extends AppCompatActivity implements UserAdapter.OnPatientListener {
    RecyclerView recyclerView;
    ArrayList<User> userArrayList;
    ArrayList<String> userId;
    UserAdapter userAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    FirebaseUser user;
    Spinner spinner_locality;
    String locality="Howrah";
    boolean isPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patients_list);
        isPatient = getIntent().getBooleanExtra("isPatient", true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data..");
        progressDialog.show();

        spinner_locality=findViewById(R.id.locality_spinner);
        ArrayAdapter<CharSequence> loc_adapter=ArrayAdapter.createFromResource(this, R.array.localities, android.R.layout.simple_spinner_item);
        loc_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_locality.setAdapter(loc_adapter);
        if(isPatient)
        {
            spinner_locality.setVisibility(View.GONE);
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userArrayList = new ArrayList<>();
        userAdapter = new UserAdapter(PatientsView.this, userArrayList, this);      //Creates a new Adapter with current Activity as context
        recyclerView.setAdapter(userAdapter);
        db = FirebaseFirestore.getInstance();
        userId = new ArrayList<>();
        spinner_locality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                locality = (String) adapterView.getItemAtPosition(i);
                EventChangeListener2();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //For re-rendering the RecyclerView when some change is made (Patient Data is Edited/Deleted)
    @Override
    public void onResume()
    {
        super.onResume();
        if(isPatient)
            EventChangeListener();
        else
            EventChangeListener2();
    }

    private void EventChangeListener() {

        userArrayList.clear();                                  //clears the existing PatientDetails and fills it up with new and updated data
        CollectionReference ref = db.collection("UserDetails").document(Objects.requireNonNull(user.getEmail())).collection("Patients");
        ref.get().addOnSuccessListener(value -> {
            for(DocumentSnapshot dc : value.getDocuments())
            {
                userArrayList.add(dc.toObject(User.class));     //Converting the DocuSnapshot to a User.class object
                userId.add(dc.getId());
            }
            userAdapter.notifyDataSetChanged();
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        });
    }

    private void EventChangeListener2() {
        userArrayList.clear();                                  //clears the existing PatientDetails and fills it up with new and updated data
        CollectionReference ref = db.collection("UserDetails");
        ref.whereEqualTo("isWorker","false").get().addOnSuccessListener(value -> {
            for(DocumentSnapshot dc : value.getDocuments()) {
                    CollectionReference colRef = dc.getReference().collection("Patients");
                    colRef.whereEqualTo("locality",locality).get().addOnSuccessListener(v -> {
                        for (DocumentSnapshot doc : v.getDocuments()) {
                                userArrayList.add(doc.toObject(User.class));     //Converting the DocuSnapshot to a User.class object
                                userId.add(doc.getId());

                        }
                        userAdapter.notifyDataSetChanged();
                    });
            }
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        });
    }

    public void backToDashboard(View view)
    {
        super.finish();
    }

    //defines the onPatientClick method of the interface: onPatientListener
    @Override
    public void onPatientClick(int position) {
        Intent intent = new Intent(getApplicationContext(), ReportsActivity.class);
        intent.putExtra("user", userArrayList.get(position));   //passes a User object by encoding it in a Parcel
        intent.putExtra("id", userId.get(position));
        startActivity(intent);
    }
}
