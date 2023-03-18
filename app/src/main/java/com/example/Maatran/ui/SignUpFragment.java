package com.example.Maatran.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.Maatran.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String IS_WORKER = "isWorker";

    // TODO: Rename and change types of parameters
    private boolean isWorker;

    public SignUpFragment() {
        super(R.layout.fragment_signup);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param isWorker Parameter 1.
     * @return A new instance of fragment SignUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance(boolean isWorker) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_WORKER, isWorker);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isWorker = getArguments().getBoolean(IS_WORKER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        if(!isWorker)
        {
            rootView.findViewById(R.id.signup_empid_box).setVisibility(View.GONE);
            rootView.findViewById(R.id.signup_hospital_box).setVisibility(View.GONE);
        }
        return rootView;
    }


}