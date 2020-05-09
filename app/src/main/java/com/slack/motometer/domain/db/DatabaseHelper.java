package com.slack.motometer.domain.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    //// Database Information - start

    // Database Name
    static final String DB_NAME = "PROFILES.DB";
    // Database Version
    static final int DB_VERSION = 1;

    //// Profile Table
    // Table Name
    public static final String PROFILE_TABLE_NAME = "PROFILES";
    // Table Columns
    public static final String _ID = "_id";
    public static final String YEAR = "year";
    public static final String MAKE = "make";
    public static final String MODEL = "model";
    public static final String HOURS = "hours";

    //// Task Table
    // Table Name
    public static final String TASK_TABLE_NAME = "TASKS";
    // Table Columns
    // _ID = "_id" // primary key autoincrement
    public static final String PROFILE_ID = "profile_id";
    public static final String TASK_TITLE = "title";
    public static final String INTERVAL = "interval";
    public static final String LAST_COMPLETED_AT = "last_at";

    //// Checklist Table
    // Table Name
    public static final String CHECKLIST_TABLE_NAME = "CHECKLIST";
    // Table Columns
    // _ID = "_id" // primary key autoincrement
    // PROFILE_ID = "profile_id" // foreign key Profile.id
    public static final String CL_ITEM_TITLE = "cl_item_title";
    public static final String CL_ITEM_COMPLETE = "cl_item_complete";

    //// Notes Table
    // Table Name
    public static final String NOTES_TABLE_NAME = "NOTES";
    // Table Columns
    // _ID = "_id" // primary key autoincrement
    // PROFILE_ID = "profile_id" // foreign key Profile.id
    public static final String NOTES_CONTENT = "content";

    //// Images Table
    // Table Name
    public static final String IMAGES_TABLE_NAME = "IMAGES";
    // Table Columns
    // _ID = "_id" // primary key autoincrement
    // PROFILE_ID = "profile_id" // foreign key Profile.id
    public static final String IMAGE_PATH = "image_path";

    //// Database Information - end


    //// Table creation query strings - start

    // Create Profile table query string
    private static final String CREATE_PROFILE_TABLE = "create table " + PROFILE_TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + YEAR + " TEXT NOT NULL, "
            + MAKE + " TEXT NOT NULL, "
            + MODEL + " TEXT NOT NULL, "
            + HOURS + " TEXT NOT NULL)";

    // Create Task table query string
    private static final String CREATE_TASK_TABLE = "create table " + TASK_TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PROFILE_ID + " INTEGER, "
            + TASK_TITLE + " TEXT NOT NULL, "
            + INTERVAL + " TEXT NOT NULL, "
            + LAST_COMPLETED_AT + " TEXT NOT NULL, "
            + " FOREIGN KEY(" + PROFILE_ID + ") REFERENCES profiles(" + _ID + "))";

    // Create Checklist table query string
    private static final String CREATE_CHECKLIST_TABLE = "create table " + CHECKLIST_TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PROFILE_ID + " INTEGER, "
            + CL_ITEM_TITLE + " TEXT NOT NULL, "
            + CL_ITEM_COMPLETE + " INTEGER DEFAULT 0, "
            + " FOREIGN KEY(" + PROFILE_ID + ") REFERENCES profiles(" + _ID + "))";

    // Create Notes table query string
    private static final String CREATE_NOTES_TABLE = "create table " + NOTES_TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PROFILE_ID + " INTEGER, "
            + NOTES_CONTENT + " TEXT NOT NULL, "
            + " FOREIGN KEY(" + PROFILE_ID + ") REFERENCES profiles(" + _ID + "))";

    // Create Images table query string
    private static final String CREATE_IMAGES_TABLE = "create table " + IMAGES_TABLE_NAME + "("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PROFILE_ID + " INTEGER, "
            + IMAGE_PATH + " TEXT NOT NULL, "
            + " FOREIGN KEY(" + PROFILE_ID + ") REFERENCES profiles(" + _ID + "))";

    //// Table creation query strings - end


    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Create tables in onCreate
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROFILE_TABLE);
        db.execSQL(CREATE_TASK_TABLE);
        db.execSQL(CREATE_CHECKLIST_TABLE);
        db.execSQL(CREATE_NOTES_TABLE);
        db.execSQL(CREATE_IMAGES_TABLE);
    }

    // For now, onUpgrade - simply drop all tables and recreate using onCreate
    // Fix if/when migrations learned
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PROFILE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CHECKLIST_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + IMAGES_TABLE_NAME);
        onCreate(db);
    }

    // Helper method for dev work - clear a table
    public void clearTable(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, null, null);
    }

}
