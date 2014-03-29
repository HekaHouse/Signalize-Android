package ppc.signalize.perspectives.content.data.types;

import android.database.Cursor;

public class AdmissionData {
    public int id;
    public PatientData patient;
    public String physician_name;
    public String medical_specialty;
    public String medical_specialty_detail;
    public String drg;
    public String unit;
    public String nurse_station;
    public String admit;
    public String discharge;
    public int emergency;
    public int unexpected;
    public int roommate;
    public int special_diet;
    public String health_rating;

    public AdmissionData(Cursor cursor, PatientData p) {
        id = cursor.getInt(0);
        physician_name = cursor.getString(1);
        medical_specialty = cursor.getString(2);
        medical_specialty_detail = cursor.getString(3);
        drg = cursor.getString(4);
        unit = cursor.getString(5);
        nurse_station = cursor.getString(5);
        admit = cursor.getString(6);
        discharge = cursor.getString(7);
        emergency = cursor.getInt(8);
        unexpected = cursor.getInt(9);
        roommate = cursor.getInt(10);
        special_diet = cursor.getInt(11);
        health_rating = cursor.getString(12);
        patient = p;
    }
}