package ppc.signalize.perspectives.content.data.types;

import android.database.Cursor;

public class FeedbackData {
    public int id;
    public int tier;
    public Double severity;
    public Double sentiment;
    public String comment;
    public String received;
    public int happiness_index;
    public String area_of_concern;
    public String feedback_type;
    public AdmissionData admission;

    public FeedbackData(Cursor cursor, AdmissionData a) {
        id = cursor.getInt(0);
        tier = cursor.getInt(1);
        severity = cursor.getDouble(2);
        sentiment = cursor.getDouble(3);
        comment = cursor.getString(4).replaceAll("[\"\\\\]", "");
        received = cursor.getString(5);
        happiness_index = cursor.getInt(6);
        area_of_concern = cursor.getString(7);
        feedback_type = cursor.getString(8);
        admission = a;
    }
}