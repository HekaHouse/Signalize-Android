package ppc.signalize.perspectives.content.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Aron on 3/26/14.
 */
public class SQLiteOpener extends SQLiteOpenHelper {


    protected static final String DATABASE_NAME = "feedback.db";
    protected static final int DATABASE_VERSION = 1;

    protected static final String TABLE_FEEDBACK = "feedback";
    protected static final String TABLE_ADMISSION = "admission";
    protected static final String TABLE_PATIENT = "patient";

    protected static final String COL_TIER = "tier";
    protected static final String COL_SEVERITY = "severity";
    protected static final String COL_SENTIMENT = "sentiment";
    protected static final String COL_COMMENT = "comment";
    protected static final String COL_RECEIVED = "received";
    protected static final String COL_HAPPINESS_INDEX = "happiness_index";
    protected static final String COL_AREA_OF_CONCERN = "area_of_concern";
    protected static final String COL_FEEDBACK_TYPE = "feedback_type";
    protected static final String COL_ADMISSION = "admission_id";

    protected static final String COL_PHYSICIAN_NAME = "physician_name";
    protected static final String COL_MEDICAL_SPECIALTY = "medical_specialty";
    protected static final String COL_MEDICAL_SPECIALTY_DETAIL = "medical_specialty_detail";
    protected static final String COL_DRG = "drg";
    protected static final String COL_UNIT = "unit";
    protected static final String COL_NURSE_STATION = "nurse_station";
    protected static final String COL_ADMIT = "admit";
    protected static final String COL_DISCHARGE = "discharge";
    protected static final String COL_EMERGENCY = "emergency";
    protected static final String COL_UNEXPECTED = "unexpected";
    protected static final String COL_ROOMMATE = "roommate";
    protected static final String COL_SPECIAL_DIET = "special_diet";
    protected static final String COL_HEALTH_RATING = "health_rating";
    protected static final String COL_PATIENT = "patient_id";

    protected static final String COL_PATIENT_NAME = "patient_name";
    protected static final String COL_PATIENT_REFERENCE = "patient_reference";
    protected static final String COL_PATIENT_GENDER = "patient_gender";
    protected static final String COL_PATIENT_BIRTHDATE = "patient_birthdate";

    // Database creation sql statement
    protected static final String CREATE_PATIENT = "create table patient " +
            "(id integer primary key autoincrement, " +
            " patient_name text, " +
            " patient_reference text, " +
            " patient_gender text, " +
            " patient_birthdate text, " +
            " unique (patient_reference) on conflict ignore);";

    protected static final String CREATE_ADMISSION = "create table admission " +
            "(id integer primary key autoincrement, " +
            " physician_name text, " +
            " medical_specialty text, " +
            " medical_specialty_detail text, " +
            " drg text, " +
            " unit text, " +
            " nurse_station text, " +
            " admit text," +
            " discharge text," +
            " health_rating text," +
            " emergency integer," +
            " unexpected integer," +
            " roommate integer," +
            " special_diet integer," +
            " patient_id integer," +
            " unique (patient_id, admit) on conflict ignore, " +
            " foreign key(patient_id) REFERENCES patient(_id) ON DELETE CASCADE ON UPDATE CASCADE);";
    protected static final String CREATE_FEEDBACK = "create table feedback " +
            "(id integer primary key autoincrement, " +
            " comment text, " +
            " tier integer, " +
            " severity real, " +
            " sentiment real, " +
            " received text, " +
            " happiness_index integer, " +
            " area_of_concern text," +
            " feedback_type text," +
            " admission_id integer," +
            " unique (admission_id, feedback_type, received) on conflict ignore, " +
            " foreign key(admission_id) REFERENCES admission(_id) ON DELETE CASCADE ON UPDATE CASCADE);";

    public SQLiteOpener(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_PATIENT);
        database.execSQL(CREATE_ADMISSION);
        database.execSQL(CREATE_FEEDBACK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteOpener.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDBACK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMISSION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENT);
        onCreate(db);
    }

}