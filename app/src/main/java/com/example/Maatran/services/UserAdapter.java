package com.example.Maatran.services;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Maatran.R;

import java.util.ArrayList;

//Adapter class for implementing Recycler View
//For more documentation, goto: https://developer.android.com/guide/topics/ui/layout/recyclerview#java
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    Context context;
    ArrayList<User> userArrayList;              //ArrayList containing the User.class objects to be displayed on the list
    private OnPatientListener onPatientListener;

    public UserAdapter(Context context, ArrayList<User> userArrayList, OnPatientListener onPatientListener) {
        this.context = context;
        this.userArrayList = userArrayList;
        this.onPatientListener = onPatientListener;
    }

    //Used for creating and initializing a ViewHolder object that is wrapped around a View
    //This only creates the Views, or the layouts, and does not bind any data
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.patient_item, parent, false);
        //A new view is created for an item in the list
        return new UserViewHolder(v, onPatientListener);
    }

    //Used for binding data with the initialized ViewHolder objects
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userArrayList.get(position);        //gets the user object present at ArrayList<User>[position]
        holder.name.setText("NAME: "+user.getName());
        holder.age.setText("AGE: "+ user.getAge());
        holder.gender.setText("GENDER: "+user.getGender());
        if(user.getLocality()!=null)
            holder.locality.setText("LOCALITY: "+user.getLocality());
        else
            holder.locality.setText("LOCALITY: Not Available");
        holder.profilePic.setImageDrawable(setProfilePic(user, user.getGender(), (int) user.getAge()));
    }

    //Used for getting the size of the data set (Number of User objects in userArrayList)
    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public Drawable setProfilePic(User user, String lgen, int age)
    {
        String gen = lgen.equalsIgnoreCase("male") ?"m":(lgen.equalsIgnoreCase("female")?"w":"o");
        if(!gen.equals("o"))
            gen = gen.equals("m")?(age>20?"m":"b"):(age>20?"w":"g");
        String uri = gen.equals("o")?"@drawable/profile_ico_white":"@drawable/"+"p"+"_"+gen;
        int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
        Drawable res = context.getResources().getDrawable(imageResource);
        return res;
    }

    //ViewHolder class for defining a ViewHolder object
    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, age, gender, locality;
        OnPatientListener onPatientListener;
        ImageView profilePic;

        //Instantiates UserViewHolder with a View object
        public UserViewHolder(@NonNull View itemView, OnPatientListener onPatientListener) {
            super(itemView);
            name = itemView.findViewById(R.id.patient_name_1);
            age = itemView.findViewById(R.id.patient_age_1);
            gender = itemView.findViewById(R.id.patient_gender_1);
            profilePic = itemView.findViewById(R.id.profilePicListItem);
            locality = itemView.findViewById(R.id.patient_address_1);
            this.onPatientListener = onPatientListener;
            itemView.setOnClickListener(this);              //onClick listener for the View
        }

        //Defines the onClick event for each ViewHolder object
        @Override
        public void onClick(View view) {
            onPatientListener.onPatientClick(getBindingAdapterPosition());          //gets the position of the ViewHolder object that is clicked
        }
    }

    //defines the interface for the ViewHolder object
    public interface OnPatientListener {
        void onPatientClick(int position);
    }
}
