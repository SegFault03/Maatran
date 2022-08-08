package com.example.maatran;

public class CardDetails {
    String patient_name,patient_temperature,patient_SpO2,patient_pressure,patient_sugar;
    int report_number;

    public CardDetails(String patient_name, String patient_temperature, String patient_SpO2, String patient_pressure, String patient_sugar, int report_number) {
        this.patient_name = patient_name;
        this.patient_temperature = patient_temperature;
        this.patient_SpO2 = patient_SpO2;
        this.patient_pressure = patient_pressure;
        this.patient_sugar = patient_sugar;
        this.report_number = report_number;
    }


}
