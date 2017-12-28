package com.example.joe.cst2335finalgroupproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Global Database Helper to avoid the creation of multiple small databases
 * @author 		Joe Ireland
 * @version		2
 */
public class m_GlobalDatabaseHelper extends SQLiteOpenHelper {

    //Car Database and Column Names
    public static final String FUEL_DETAILS_TABLE = "FUEL_DETAILS_TABLE";
    public static final String KEY_ID = "_id";
    public static final String KEY_PRICE = "price";
    public static final String KEY_LITRES = "litres";
    public static final String KEY_KILOMETERS = "kilometers";
    public static final String KEY_DATE = "date";
    public static final String CREATE_TABLE_SQL
            = "CREATE TABLE " + FUEL_DETAILS_TABLE + " ( "
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_PRICE + " REAL, "
            + KEY_LITRES + " REAL, "
            + KEY_KILOMETERS + " REAL, "
            + KEY_DATE + " INTEGER"
            + " );";  // can convert to timestamp later
    public static final String DROP_FUEL_TABLE_SQL = "DROP TABLE IF EXISTS " + FUEL_DETAILS_TABLE;
    public static final String C_SELECT_ALL_SQL
            = String.format("SELECT * FROM %s ORDER BY %s", FUEL_DETAILS_TABLE, KEY_DATE);
    //Database and Table Names
    static final String DATABASE_NAME = "GlobalDatabase.db";
    //Thermostat Database and Column Names
    static final String THERMOSTAT_TABLE_NAME = "THERMOSTAT_RULES";
    public static final String DROP_THERMOSTAT_TABLE_SQL = "DROP TABLE IF EXISTS " + THERMOSTAT_TABLE_NAME;
    static final String RULE_ID = "RULE_ID";
    static final String RULE_COL_NAME = "RULES";
    public static final String CREATE_THERMOSTAT_TABLE_SQL
            = "CREATE TABLE " + THERMOSTAT_TABLE_NAME + " ( "
            + RULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RULE_COL_NAME + " TEXT "
            + " );";
    //Nurition Database and Column Names
    static final String NUTRITION_TABLE_NAME = "NUTRITION_INFO";
    public static final String DROP_NUTRITION_TABLE_SQL = "DROP TABLE IF EXISTS " + NUTRITION_TABLE_NAME;
    static final String FOOD_ID = "FOOD_ID";
    static final String FOOD_ITEM_COL_NAME = "FOOD_NAME";
    static final String CALORIES_COL_NAME = "CALORIES";
    static final String CARB_COL_NAME = "CARBOHYDRATE";
    static final String FAT_COL_NAME = "FAT";
    public static final String CREATE_NUTRITION_TABLE_SQL
            = "CREATE TABLE " + NUTRITION_TABLE_NAME + " ( "
            + FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FOOD_ITEM_COL_NAME + " TEXT, "
            + CALORIES_COL_NAME + " INTEGER, "
            + CARB_COL_NAME + " INTEGER, "
            + FAT_COL_NAME + " INTEGER "
            + " );";

    //Activity Database and Column Names
    static final String ACTIVITY_TABLE_NAME = "ACTIVITY_LOG";
    public static final String DROP_ACTIVITY_TABLE_SQL = "DROP TABLE IF EXISTS " + ACTIVITY_TABLE_NAME;
    static final String WORKOUT_ID = "WORKOUT_ID";
    static final String TYPE_COL_NAME = "WORKOUT_TYPE";
    static final String DURATION_COL_NAME = "WORKOUT_DURATION";
    static final String NOTE_COL_NAME = "WORKOUT_NOTES";
    static final String TIME_COL_NAME = "TIMESTAMP";
    public static final String CREATE_ACTIVITY_TABLE_SQL
            = "CREATE TABLE " + ACTIVITY_TABLE_NAME + " ( "
            + WORKOUT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TYPE_COL_NAME + " INTEGER, "
            + DURATION_COL_NAME + " INTEGER, "
            + NOTE_COL_NAME + " INTEGER, "
            + TIME_COL_NAME + " DATETIME DEFAULT CURRENT_TIMESTAMP "
            + " );";

    /** Database Version Number */
    private static final int DATABASE_VERSION_NUM = 1;  //WARNING: AVOID ALTERING THIS VALUE UNLESS REQUIRED. THIS WILL CLEAR ALL TABLES FOR ALL ACTIVITIES.


    /**
     * Constructor
     * @param ctx
     */
    m_GlobalDatabaseHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION_NUM);
    }

    /**
     * Creates new tables in the database
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_THERMOSTAT_TABLE_SQL);
        db.execSQL(CREATE_NUTRITION_TABLE_SQL);
        db.execSQL(CREATE_TABLE_SQL);
        db.execSQL(CREATE_ACTIVITY_TABLE_SQL);
    }

    /**
     * Drops and recreates tables if version is upgraded
     * @param db
     * @param oldVer
     * @param newVer
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        db.execSQL(DROP_THERMOSTAT_TABLE_SQL);
        db.execSQL(DROP_NUTRITION_TABLE_SQL);
        db.execSQL(DROP_FUEL_TABLE_SQL);
        db.execSQL(DROP_ACTIVITY_TABLE_SQL);
        onCreate(db);
    }

    /**
     * Drops and recreates tables if version is downgraded
     * @param db
     * @param oldVer
     * @param newVer
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVer, int newVer) {
        db.execSQL(DROP_THERMOSTAT_TABLE_SQL);
        db.execSQL(DROP_NUTRITION_TABLE_SQL);
        db.execSQL(DROP_FUEL_TABLE_SQL);
        db.execSQL(DROP_ACTIVITY_TABLE_SQL);
        onCreate(db);
    }
}
