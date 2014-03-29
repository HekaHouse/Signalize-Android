package ppc.signalize.perspectives.content.data.types;

import android.database.Cursor;

public class PatientData {
    public int id;
    public String patient_name;
    public String patient_reference;
    public String patient_gender;
    public String patient_birthdate;

    public PatientData(Cursor cursor) {
        id = cursor.getInt(0);
        patient_name = cursor.getString(1);
        patient_reference = cursor.getString(2);
        patient_gender = cursor.getString(3);
        patient_birthdate = cursor.getString(4);
    }
}