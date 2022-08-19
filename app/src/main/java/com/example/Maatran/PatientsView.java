package com.example.Maatran;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PatientsView extends AppCompatActivity implements UserAdapter.OnPatientListener {
    RecyclerView recyclerView;
    ArrayList<User> userArrayList;
    ArrayList<String> userId;
    UserAdapter userAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patients_list);
        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data..");
        progressDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userArrayList = new ArrayList<>();
        userAdapter = new UserAdapter(PatientsView.this, userArrayList, this);
        recyclerView.setAdapter(userAdapter);

        db = FirebaseFirestore.getInstance();
        userId = new ArrayList<>();

        EventChangeListener(user);


    }

    private void EventChangeListener(FirebaseUser user) {

        CollectionReference ref = db.collection("UserDetails").document(user.getEmail()).collection("Patients");
        ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot value) {
                for(DocumentSnapshot dc : value.getDocuments())
                {
                    userArrayList.add(dc.toObject(User.class));
                    userId.add(dc.getId());
                }
                userAdapter.notifyDataSetChanged();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }

    public void backToDashboard(View view)
    {
        super.finish();
    }

    @Override
    public void onPatientClick(int position) {
        Intent intent = new Intent(getApplicationContext(), ReportsActivity.class);
        intent.putExtra("user", userArrayList.get(position));
        intent.putExtra("id", userId.get(position));
        startActivity(intent);
    }
}
