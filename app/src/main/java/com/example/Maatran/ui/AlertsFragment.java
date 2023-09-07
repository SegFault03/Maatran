package com.example.Maatran.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Maatran.R;
import com.example.Maatran.services.User;
import com.example.Maatran.services.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlertsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlertsFragment extends DialogFragment implements UserAdapter.OnPatientListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    RecyclerView recyclerView;
    ArrayList<User> userArrayList;
    ArrayList<String> userId;
    UserAdapter userAdapter;
    FirebaseFirestore db;
    FirebaseUser user;
    private static final String ARG_PARAM1 = "isPatient";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private boolean isPatient;
    private boolean isAlerts;

    public AlertsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AlertsFragment newInstance(boolean isPatient, boolean isAlerts) {
        AlertsFragment fragment = new AlertsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, isPatient);
        args.putBoolean(ARG_PARAM2, isAlerts);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isPatient = getArguments().getBoolean(ARG_PARAM1);
            isAlerts = getArguments().getBoolean(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alerts, container, false);

        db = FirebaseFirestore.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();
        userId = new ArrayList<>();
        // Initialize the ListView and ArrayAdapter
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userArrayList = new ArrayList<>();
        userAdapter = new UserAdapter(getActivity(), userArrayList, this);      //Creates a new Adapter with current Activity as context
        recyclerView.setAdapter(userAdapter);
        Button btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog
                dismiss();
            }
        });
        TextView tv=view.findViewById(R.id.heading);
        if(isAlerts) {
            tv.setText("Alerts");
            if (isPatient)
                fetchFamilyAlerts("SosAlerts");
            else fetchAllAlerts("SosAlerts");
        }
        else {
            tv.setText("High Risk");
            if (isPatient)
                fetchFamilyAlerts("HighRisks");
            else fetchAllAlerts("HighRisks");
        }
        return view;
    }

    void fetchAllAlerts(String name)
    {
        userArrayList.clear();
        db.collection(name).get().addOnSuccessListener(v->{
           for(DocumentSnapshot ds:v.getDocuments())
           {
               db.collection("UserDetails").document(ds.getId()).get().addOnSuccessListener(d->{
                   userArrayList.add(d.toObject(User.class));     //Converting the DocuSnapshot to a User.class object
                   userId.add(d.getId());
                   Log.v("TAG", d.getId());
                   userAdapter.notifyDataSetChanged();
               });
           }
        });
    }

    void fetchFamilyAlerts(String name)
    {
        userArrayList.clear();                                  //clears the existing PatientDetails and fills it up with new and updated data
        DocumentReference ref = db.collection("UserDetails").document(Objects.requireNonNull(user.getEmail()));
        ref.get().addOnSuccessListener(value -> {
            db.collection("UserDetails")
                    .document(Objects.requireNonNull(Objects.requireNonNull(value.getData()).get("admin_id")).toString())
                    .collection("Patients")
                    .get()
                    .addOnSuccessListener(v -> {
                        db.collection(name).get().addOnSuccessListener(val->{
                        for (DocumentSnapshot dc : v.getDocuments()) {
                            for(DocumentSnapshot d:val.getDocuments()) {
                                if(d.getId().equals(dc.getId())) {
                                    db.collection("UserDetails").document(dc.getId())
                                            .get().addOnSuccessListener(doc -> {
                                                userArrayList.add(doc.toObject(User.class));     //Converting the DocuSnapshot to a User.class object
                                                userId.add(dc.getId());
                                                Log.v("TAG", dc.getId());
                                                userAdapter.notifyDataSetChanged();

                                            });
                                }
                            }
                        }
                        });
                    });
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onPatientClick(int position) {
        Intent intent = new Intent(getContext(), ReportsActivity.class);
        intent.putExtra("user", userArrayList.get(position));   //passes a User object by encoding it in a Parcel
        intent.putExtra("id", userId.get(position));
        startActivity(intent);
    }
}