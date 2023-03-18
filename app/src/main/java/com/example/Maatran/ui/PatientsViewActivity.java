package com.example.Maatran.ui;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Maatran.R;
import com.example.Maatran.services.User;
import com.example.Maatran.services.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

//Activity that initializes the RecyclerView
//The interface onPatientListener is defined in UserAdapter.class
public class PatientsViewActivity extends AppCompatActivity implements UserAdapter.OnPatientListener {
    RecyclerView recyclerView;
    LinearLayout options_layout;
    ArrayList<User> userArrayList;
    ArrayList<String> userId;
    UserAdapter userAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    FirebaseUser user;
    Spinner spinner_locality;
    String locality="Howrah";
    String family_id="";
    String email="";
    boolean isPatient;
    boolean isSuccessful=false;
    private int f=0;

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
        options_layout=findViewById(R.id.options_layout);
        ArrayAdapter<CharSequence> loc_adapter=ArrayAdapter.createFromResource(this, R.array.localities, android.R.layout.simple_spinner_item);
        loc_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner_locality.setAdapter(loc_adapter);
        if(isPatient)
            spinner_locality.setVisibility(View.GONE);
        else
            options_layout.setVisibility(View.GONE);

        user = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userArrayList = new ArrayList<>();
        userAdapter = new UserAdapter(PatientsViewActivity.this, userArrayList, this);      //Creates a new Adapter with current Activity as context
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
        if(isPatient) {
            fetchDetails();
        }
    }

    public void fetchDetails()
    {
        DocumentReference ref = db.collection("UserDetails").document(Objects.requireNonNull(user.getEmail()));
        ref.get().addOnSuccessListener(value -> {
            if(value.get("admin_id").toString().equals("null"))
            {
                recyclerView.setVisibility(View.INVISIBLE);
                f=1;
            }
            else
            {
                recyclerView.setVisibility(View.VISIBLE);
                TextView tv=findViewById(R.id.create_family_txt);
                tv.setText("GET KEY");
                ConstraintLayout join_family_layout=findViewById(R.id.join_family_layout);
                join_family_layout.setVisibility(View.GONE);
                f=0;
                EventChangeListener();
            }
        });
        progressDialog.dismiss();
    }

    public void joinFamily(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View popupFamilyDetails = inflater.inflate(R.layout.family_details,null);
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        // lets taps outside the popupWindow dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupFamilyDetails, width, height, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        Button join = popupFamilyDetails.findViewById(R.id.btn_join);
        Button cancel = popupFamilyDetails.findViewById(R.id.btn_cancel);
        join.setOnClickListener(v -> {

            String email = ((EditText)popupFamilyDetails.findViewById(R.id.admin_id)).getText().toString().trim();
            String family_id = ((EditText)popupFamilyDetails.findViewById(R.id.family_key)).getText().toString().trim();

            if(email=="" || family_id=="")
            {
                Toast.makeText(this, "You must enter a valid admin email and family key.", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
                return;
            }

            DocumentReference ref = db.collection("UserDetails").document(Objects.requireNonNull(email));
            HashMap<String, String> mp = new HashMap<>();
            mp.put("email", user.getEmail());


            ref.get().addOnSuccessListener(value -> {
                if(value.getData().get("family_id").toString().equals(family_id))
                {
                    ref.collection("Patients").document(user.getEmail())
                            .set(mp)
                            .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully written!"))
                            .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));

                    mp.clear();
                    mp.put("admin_id", email);
                    mp.put("family_id", family_id);

                    db.collection("UserDetails")
                            .document(Objects.requireNonNull(user.getEmail()))
                            .set(mp, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully written!"))
                            .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));
                }
                else Toast.makeText(getApplicationContext(), "Invalid credentials.", Toast.LENGTH_SHORT).show();
            });
            f=0;
            popupWindow.dismiss();
            fetchDetails();
        });
        cancel.setOnClickListener(v -> popupWindow.dismiss());

    }

    public void familyKeyHandler(View view)
    {
        if(f == 1)
            createFamily(view);
        else
        {
            DocumentReference ref = db.collection("UserDetails").document(Objects.requireNonNull(user.getEmail()));
            ref.get().addOnSuccessListener(value -> {
                displayKey(value.get("family_id").toString(), view);
            });
        }
    }

    public void displayKey(String key, View view) {
        LayoutInflater inflater = getLayoutInflater();
        View popupCopyKey = inflater.inflate(R.layout.copy_key,null);
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        // lets taps outside the popupWindow dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupCopyKey, width, height, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        TextView tv=popupCopyKey.findViewById(R.id.family_key);
        tv.setText(key);
        Button copy = popupCopyKey.findViewById(R.id.btn_copy);
        Button cancel = popupCopyKey.findViewById(R.id.btn_cancel);
        copy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", key);
            clipboard.setPrimaryClip(clip);
            popupWindow.dismiss();
        });
        cancel.setOnClickListener(v -> popupWindow.dismiss());
    }

    public void createFamily(View view) {
        String family_id = UUID.randomUUID().toString();
        displayKey(family_id, view);
        HashMap<String, String> mp = new HashMap<>();
        DocumentReference ref = db.collection("UserDetails").document(Objects.requireNonNull(user.getEmail()));

        mp.put("admin_id", user.getEmail());
        mp.put("family_id", family_id);
        ref.set(mp, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.e("TAG", "Error writing document", e));

        mp.clear();
        mp.put("email", user.getEmail());
        ref.collection("Patients")
                .document(user.getEmail())
                .set(mp)
                .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.e("TAG", "Error writing document", e));
        f=0;
        fetchDetails();
    }

    private void EventChangeListener() {
        userArrayList.clear();                                  //clears the existing PatientDetails and fills it up with new and updated data
        DocumentReference ref = db.collection("UserDetails").document(Objects.requireNonNull(user.getEmail()));
        ref.get().addOnSuccessListener(value -> {
            db.collection("UserDetails")
                    .document(Objects.requireNonNull(Objects.requireNonNull(value.getData()).get("admin_id")).toString())
                    .collection("Patients")
                    .get()
                    .addOnSuccessListener(v -> {
                        for (DocumentSnapshot dc : v.getDocuments()) {
                            db.collection("UserDetails").document(dc.getId())
                                    .get().addOnSuccessListener(doc -> {
                                        userArrayList.add(doc.toObject(User.class));     //Converting the DocuSnapshot to a User.class object
                                        userId.add(dc.getId());
                                        Log.v("TAG", dc.getId());
                                        userAdapter.notifyDataSetChanged();
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                    });
                        }
                    });
        });
    }

    private void EventChangeListener2() {
        userArrayList.clear();                                  //clears the existing PatientDetails and fills it up with new and updated data
        CollectionReference ref = db.collection("UserDetails");
        if(!locality.equals("Show all")) {
            ref.whereEqualTo("isWorker", "false").whereEqualTo("locality", locality).get().addOnSuccessListener(value -> {
                for (DocumentSnapshot dc : value.getDocuments()) {
                    userArrayList.add(dc.toObject(User.class));     //Converting the DocuSnapshot to a User.class object
                    userId.add(dc.getId());
                }
                userAdapter.notifyDataSetChanged();
            });
        }
        else
        {
            ref.whereEqualTo("isWorker", "false").get().addOnSuccessListener(value2->{
                for(DocumentSnapshot ds : value2.getDocuments())
                {
                    userArrayList.add(ds.toObject(User.class));     //Converting the DocuSnapshot to a User.class object
                    userId.add(ds.getId());
                }
                userAdapter.notifyDataSetChanged();
            });

        }
        if (progressDialog.isShowing())
            progressDialog.dismiss();
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
