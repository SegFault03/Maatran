package com.example.Maatran;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    Context context;
    ArrayList<User> userArrayList;
    private OnPatientListener onPatientListener;

    public UserAdapter(Context context, ArrayList<User> userArrayList, OnPatientListener onPatientListener) {
        this.context = context;
        this.userArrayList = userArrayList;
        this.onPatientListener = onPatientListener;
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.patient_item, parent, false);

        return new UserViewHolder(v, onPatientListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = userArrayList.get(position);

        holder.name.setText("NAME: "+user.getName());
        holder.age.setText("AGE: "+Long.toString(user.getAge()));
        holder.gender.setText("GENDER: "+user.getGender());
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, age, gender;
        OnPatientListener onPatientListener;

        public UserViewHolder(@NonNull View itemView, OnPatientListener onPatientListener) {
            super(itemView);
            name = itemView.findViewById(R.id.patient_name_1);
            age = itemView.findViewById(R.id.patient_age_1);
            gender = itemView.findViewById(R.id.patient_gender_1);
            this.onPatientListener = onPatientListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onPatientListener.onPatientClick(getAdapterPosition());
        }
    }

    public interface OnPatientListener {
        void onPatientClick(int position);
    }
}
