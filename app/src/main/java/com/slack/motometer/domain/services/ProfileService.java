package com.slack.motometer.domain.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.slack.motometer.domain.db.DatabaseHelper;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.model.ProfileImage;
import com.slack.motometer.domain.repositories.ImageRepository;
import com.slack.motometer.domain.repositories.ProfileRepository;

import java.util.ArrayList;

import static com.slack.motometer.domain.db.DatabaseHelper.HOURS;
import static com.slack.motometer.domain.db.DatabaseHelper.MAKE;
import static com.slack.motometer.domain.db.DatabaseHelper.MODEL;
import static com.slack.motometer.domain.db.DatabaseHelper.NOTES_CONTENT;
import static com.slack.motometer.domain.db.DatabaseHelper.NOTES_TABLE_NAME;
import static com.slack.motometer.domain.db.DatabaseHelper.PROFILE_ID;
import static com.slack.motometer.domain.db.DatabaseHelper.PROFILE_TABLE_NAME;
import static com.slack.motometer.domain.db.DatabaseHelper.YEAR;
import static com.slack.motometer.domain.db.DatabaseHelper._ID;

// SQLite implementation of ProfileRepository
public class ProfileService implements ProfileRepository {

    private DatabaseHelper dbHelper;
    private Context context;

    public ProfileService(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
        this.context = context;
    }

    @Override
    public long addProfile(Profile profile) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(YEAR, profile.getYear());
        contentValues.put(MAKE, profile.getMake());
        contentValues.put(MODEL, profile.getModel());
        contentValues.put(HOURS, profile.getHours());

        // Capture autogenerated profile id for NOTES record initialization below
        long profileId = db.insert(PROFILE_TABLE_NAME, null, contentValues);

        // Also initialize profile note record in notes table with empty string/contents
        contentValues.clear();
        contentValues.put(PROFILE_ID, profileId);
        contentValues.put(NOTES_CONTENT, "");
        db.insert(NOTES_TABLE_NAME, null, contentValues);

        // Also initialize profile image in images table with generic image
        // Using drawable ic_menu_gallery as default image
        Bitmap defaultImage = BitmapFactory.decodeResource(context.getResources(),
                android.R.drawable.ic_menu_gallery);
        ImageRepository imageRepository = new ImageService(context);
        imageRepository.addImage(new ProfileImage(null, String.valueOf(profileId),
                null, defaultImage));

        db.close();
        return profileId;
    }

    @Override
    public Profile getProfile(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Profile profile = null;
        Cursor cursor = db.query(PROFILE_TABLE_NAME, new String[]{_ID, YEAR, MAKE, MODEL, HOURS},
                _ID + "= ?", new String[]{String.valueOf(id)},
                null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            profile = new Profile(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4));
            cursor.close();
        }
        db.close();
        return profile;
    }

    @Override
    public ArrayList<Profile> getAllProfiles() {ArrayList<Profile> profiles = new ArrayList<>();
        // Select ALL query
        String selectAllQuery = "SELECT * FROM " + PROFILE_TABLE_NAME;

        // Get db
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try(Cursor cursor = db.rawQuery(selectAllQuery, null)) {
            // Loop through all rows and add to list
            if (cursor.moveToFirst()) {
                do {
                    profiles.add(new Profile(cursor.getString(0), cursor.getString(1),
                            cursor.getString(2), cursor.getString(3), cursor.getString(4)));
                } while (cursor.moveToNext());
            }
        }
        db.close();
        return profiles;
    }

    @Override
    public long updateProfile(Profile profile) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(YEAR, profile.getYear());
        contentValues.put(MAKE, profile.getMake());
        contentValues.put(MODEL, profile.getModel());
        contentValues.put(HOURS, profile.getHours());

        long numRowsUpdated = db.update(PROFILE_TABLE_NAME, contentValues, _ID + "= ?",
                new String[]{ profile.getId() });
        db.close();
        return numRowsUpdated;
    }

    @Override
    public void deleteProfile(Profile profile) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(PROFILE_TABLE_NAME, _ID + "= ?", new String[] { profile.getId() });
        db.close();
    }

}
