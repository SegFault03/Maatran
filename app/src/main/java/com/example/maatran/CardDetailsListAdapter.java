package com.example.maatran;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CardDetailsListAdapter extends ArrayAdapter<CardDetails> {

    public CardDetailsListAdapter(Context context, ArrayList<CardDetails> cardDetailsArrayList){
        super(context, R.layout.report_item,cardDetailsArrayList);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CardDetails user=getItem(position);
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.report_item,parent,false);
            TextView username=convertView.findViewById(R.id.report_small_patient_name);
            TextView temperature=convertView.findViewById(R.id.report_small_temperature);
            TextView sugar_level=convertView.findViewById(R.id.report_small_sugar);
            TextView pressure_level=convertView.findViewById(R.id.report_small_pressure);
            TextView spo2_level=convertView.findViewById(R.id.report_small_SpO2);
            username.setText(user.patient_name);
            temperature.setText(user.patient_temperature);
            sugar_level.setText(user.patient_sugar);
            pressure_level.setText(user.patient_pressure);
            spo2_level.setText(user.patient_SpO2);




        }


        return super.getView(position, convertView, parent);
    }
}
