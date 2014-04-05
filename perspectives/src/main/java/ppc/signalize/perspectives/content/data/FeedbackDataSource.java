package ppc.signalize.perspectives.content.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ppc.signalize.api.types.Admission;
import ppc.signalize.api.types.Feedback;
import ppc.signalize.api.types.Patient;
import ppc.signalize.perspectives.content.data.types.AdmissionData;
import ppc.signalize.perspectives.content.data.types.FeedbackData;
import ppc.signalize.perspectives.content.data.types.PatientData;

/**
 * Created by Aron on 3/26/14.
 */
public class FeedbackDataSource {
    // open() fields

    private SQLiteOpener dbHelper;
    private String[] feedbackColumns = {"id", "tier", "severity", "sentiment", "comment", "received", "happiness_index", "area_of_concern", "feedback_type", "admission_id"};
    private String[] admissionColumns = {"id", "physician_name", "medical_specialty", "medical_specialty_detail", "drg", "unit", "nurse_station", "admit", "discharge", "emergency", "unexpected", "roommate", "special_diet", "health_rating", "patient_id"};
    private String[] patientColumns = {"id", "patient_name", "patient_reference", "patient_gender", "patient_birthdate"};
    private SQLiteDatabase database = null;

    public FeedbackDataSource(Context context) {
        dbHelper = new SQLiteOpener(context);
    }

    public SQLiteDatabase open() throws SQLException {
        if (database == null || !database.isOpen())
            database = dbHelper.getWritableDatabase();
        return database;
    }

    public void close() {
        if (database.isOpen())
            dbHelper.close();
    }

    public FeedbackData createFeedback(Feedback comment) {
        AdmissionData a = createAdmission(comment.admission);
        ContentValues values = new ContentValues();
        values.put(SQLiteOpener.COL_TIER, comment.tier);
        values.put(SQLiteOpener.COL_SENTIMENT, comment.sentiment);
        values.put(SQLiteOpener.COL_SEVERITY, comment.severity);
        values.put(SQLiteOpener.COL_COMMENT, comment.comment);
        values.put(SQLiteOpener.COL_RECEIVED, comment.received);
        values.put(SQLiteOpener.COL_HAPPINESS_INDEX, comment.happiness_index);
        values.put(SQLiteOpener.COL_AREA_OF_CONCERN, comment.area_of_concern);
        values.put(SQLiteOpener.COL_FEEDBACK_TYPE, comment.feedback_type);
        values.put(SQLiteOpener.COL_ADMISSION, a.id);
        long insertId = open().insertWithOnConflict(SQLiteOpener.TABLE_FEEDBACK, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        Cursor cursor = open().query(SQLiteOpener.TABLE_FEEDBACK, feedbackColumns, "id = ?", new String[]{String.valueOf(insertId)}, null, null, null);
        cursor.moveToFirst();
        FeedbackData newComment = cursorToComment(cursor, a);
        close();
        return newComment;
    }

    private AdmissionData createAdmission(Admission admission) {
        PatientData p = createPatient(admission.patient);
        ContentValues values = new ContentValues();
        values.put(SQLiteOpener.COL_PHYSICIAN_NAME, admission.physician_name);
        values.put(SQLiteOpener.COL_MEDICAL_SPECIALTY, admission.medical_specialty);
        values.put(SQLiteOpener.COL_MEDICAL_SPECIALTY_DETAIL, admission.medical_specialty_detail);
        values.put(SQLiteOpener.COL_DRG, admission.drg);
        values.put(SQLiteOpener.COL_UNIT, admission.unit);
        values.put(SQLiteOpener.COL_NURSE_STATION, admission.nurse_station);
        values.put(SQLiteOpener.COL_ADMIT, admission.admit);
        values.put(SQLiteOpener.COL_DISCHARGE, admission.discharge);
        values.put(SQLiteOpener.COL_EMERGENCY, admission.emergency);
        values.put(SQLiteOpener.COL_UNEXPECTED, admission.unexpected);
        values.put(SQLiteOpener.COL_ROOMMATE, admission.roommate);
        values.put(SQLiteOpener.COL_SPECIAL_DIET, admission.special_diet);
        values.put(SQLiteOpener.COL_HEALTH_RATING, admission.health_rating);
        values.put(SQLiteOpener.COL_PATIENT, p.id);

        long insertId = open().insertWithOnConflict(SQLiteOpener.TABLE_ADMISSION, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        Cursor cursor = open().query(SQLiteOpener.TABLE_ADMISSION, admissionColumns, "id = ?", new String[]{String.valueOf(insertId)}, null, null, null);
        cursor.moveToFirst();
        AdmissionData ad = cursorToAdmission(cursor, p);
        return ad;
    }

    private PatientData createPatient(Patient patient) {

        ContentValues values = new ContentValues();
        values.put(SQLiteOpener.COL_PATIENT_NAME, patient.patient_name);
        values.put(SQLiteOpener.COL_PATIENT_REFERENCE, patient.patient_reference);
        values.put(SQLiteOpener.COL_PATIENT_GENDER, patient.patient_gender);
        values.put(SQLiteOpener.COL_PATIENT_BIRTHDATE, "");

        long insertId = open().insertWithOnConflict(SQLiteOpener.TABLE_PATIENT, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        Cursor cursor = open().query(SQLiteOpener.TABLE_PATIENT, patientColumns, "id = ?", new String[]{String.valueOf(insertId)}, null, null, null);
        cursor.moveToFirst();
        PatientData pd = cursorToPatient(cursor);
        return pd;
    }

    public void deleteFeedback(FeedbackData comment) {
        long id = comment.id;
        System.out.println("Comment deleted with id: " + id);
        open().delete(SQLiteOpener.TABLE_FEEDBACK, " id = " + id, null);
        close();
    }

    public void deleteAdmission(AdmissionData admission) {

        long id = admission.id;
        System.out.println("admission deleted with id: " + id);
        open().delete(SQLiteOpener.TABLE_ADMISSION, " id = " + id, null);
        close();
    }

    public void deletePatient(PatientData patient) {
        long id = patient.id;
        System.out.println("patient deleted with id: " + id);
        open().delete(SQLiteOpener.TABLE_PATIENT, " id = " + id, null);
        close();
    }


    public FeedbackData getComment(int index) {

        Cursor cursor = open().query(SQLiteOpener.TABLE_FEEDBACK, feedbackColumns, null, null, null, null, null);

        cursor.moveToFirst();

        if (cursor.moveToPosition(index)) {
            Cursor a_cursor = open().query(SQLiteOpener.TABLE_ADMISSION, admissionColumns, "id=?", new String[]{String.valueOf(cursor.getInt(9))}, null, null, null);
            a_cursor.moveToFirst();
            if (!a_cursor.isAfterLast()) {
                Cursor p_cursor = open().query(SQLiteOpener.TABLE_PATIENT, patientColumns, "id=?", new String[]{String.valueOf(a_cursor.getInt(14))}, null, null, null);
                p_cursor.moveToFirst();
                if (!p_cursor.isAfterLast()) {
                    return cursorToComment(cursor, cursorToAdmission(a_cursor, cursorToPatient(p_cursor)));
                }
                if (!p_cursor.isClosed())
                    p_cursor.close();
            }
            if (!a_cursor.isClosed())
                a_cursor.close();
        }
        if (!cursor.isClosed())
            cursor.close();

        close();
        return null;

    }

    public List<FeedbackData> getAllComments() {

        List<FeedbackData> comments = new ArrayList<FeedbackData>();

        Cursor p_cursor = open().query(SQLiteOpener.TABLE_PATIENT, patientColumns, null, null, null, null, null);

        p_cursor.moveToFirst();


        while (!p_cursor.isAfterLast()) {
            Cursor a_cursor = open().query(SQLiteOpener.TABLE_ADMISSION, admissionColumns, "patient_id=?", new String[]{String.valueOf(p_cursor.getInt(0))}, null, null, null);
            a_cursor.moveToFirst();
            while (!a_cursor.isAfterLast()) {
                Cursor f_cursor = open().query(SQLiteOpener.TABLE_FEEDBACK, feedbackColumns, "admission_id=?", new String[]{String.valueOf(a_cursor.getInt(0))}, null, null, null);
                f_cursor.moveToFirst();
                while (!f_cursor.isAfterLast()) {
                    FeedbackData comment = cursorToComment(f_cursor, cursorToAdmission(a_cursor, cursorToPatient(p_cursor)));
                    comments.add(comment);
                    f_cursor.moveToNext();
                }
                f_cursor.close();
                a_cursor.moveToNext();
            }
            a_cursor.close();
            p_cursor.moveToNext();
        }
        // make sure to close the cursor

        p_cursor.close();
        close();
        return comments;
    }

    private FeedbackData cursorToComment(Cursor cursor, AdmissionData a) {
        FeedbackData comment = new FeedbackData(cursor, a);
        cursor.close();
        return comment;
    }

    private AdmissionData cursorToAdmission(Cursor cursor, PatientData p) {
        AdmissionData admission = new AdmissionData(cursor, p);
        cursor.close();
        return admission;
    }

    private PatientData cursorToPatient(Cursor cursor) {
        PatientData p = new PatientData(cursor);
        cursor.close();
        return p;
    }

    public int getCommentCount() {
        Cursor c = open().query(SQLiteOpener.TABLE_FEEDBACK, feedbackColumns, null, null, null, null, null);
        int toReturn = c.getCount();
        c.close();
        close();
        return toReturn;
    }
}
