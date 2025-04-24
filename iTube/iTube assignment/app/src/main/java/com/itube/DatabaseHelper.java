package com.itube;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "itube_db";  // Changed to youtube_db
    private static final int DATABASE_VERSION = 1; // Increment version to trigger onUpgrade
    private static final String TABLE_USERS = "users";  // Keep user table name
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FULLNAME = "full_name";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_PLAYLIST = "playlist"; // New table for playlist
    private static final String COLUMN_PLAYLIST_ID = "id";  //id for playlist table
    private static final String COLUMN_USER_ID = "user_id"; // Foreign key to users table
    private static final String COLUMN_VIDEO_URL = "video_url";

    // Create user table query (Keep this)
    private static final String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_FULLNAME + " TEXT,"
            + COLUMN_USERNAME + " TEXT UNIQUE,"
            + COLUMN_PASSWORD + " TEXT" + ")";

    // Create playlist table query
    private static final String CREATE_PLAYLIST_TABLE = "CREATE TABLE " + TABLE_PLAYLIST + "("
            + COLUMN_PLAYLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_ID + " INTEGER,"
            + COLUMN_VIDEO_URL + " TEXT UNIQUE," //should be unique
            + " FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")" + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);  // Create the user table
        db.execSQL(CREATE_PLAYLIST_TABLE); // Create the playlist table
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //  Handles database schema updates.  For this example, we drop and recreate.
        Log.w("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion + ".  Old data will be destroyed.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST);
        onCreate(db);
    }

    // Method to insert user data into the database (Keep this)
    public long insertUser(String fullName, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULLNAME, fullName);
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
    }

    // Method to check if the username already exists (Keep this)
    public boolean checkUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_USERNAME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Modified Method to check if the username and password are correct and return the user ID
    public int checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_USERS + " WHERE "
                + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        int userId = -1; // Default value if user not found
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0); // Get the user ID from the first column
        }
        cursor.close();
        db.close();
        return userId;
    }

    // Method to insert video URL into the playlist table
    public long insertVideo(int userId, String videoUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_VIDEO_URL, videoUrl);
        long result = db.insert(TABLE_PLAYLIST, null, values);
        db.close();
        return result;
    }

    // Method to get all video URLs for a specific user
    public Cursor getVideosByUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PLAYLIST + " WHERE " + COLUMN_USER_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
    }
}