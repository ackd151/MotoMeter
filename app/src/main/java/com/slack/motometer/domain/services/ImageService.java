package com.slack.motometer.domain.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.slack.motometer.domain.db.DatabaseHelper;
import com.slack.motometer.domain.model.ProfileImage;
import com.slack.motometer.domain.repositories.ImageRepository;
import com.slack.motometer.utilities.DbBitmapUtility;

import static com.slack.motometer.domain.db.DatabaseHelper.IMAGE;
import static com.slack.motometer.domain.db.DatabaseHelper.IMAGES_TABLE_NAME;
import static com.slack.motometer.domain.db.DatabaseHelper.PROFILE_ID;
import static com.slack.motometer.domain.db.DatabaseHelper._ID;

public class ImageService implements ImageRepository {

    private DatabaseHelper dbHelper;

    public ImageService(Context context) { dbHelper = new DatabaseHelper(context); }

    @Override
    public ProfileImage getImageByProfileId(String profileId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(IMAGES_TABLE_NAME, new String[]{_ID, PROFILE_ID, IMAGE},
                PROFILE_ID + "= ?", new String[]{ profileId },
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Bitmap image = DbBitmapUtility.getBitmap(cursor.getBlob(2));

        return new ProfileImage(cursor.getString(0), cursor.getString(1), image);
    }

    @Override
    public int updateImage(ProfileImage profileImage) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        byte[] image = DbBitmapUtility.getByteArray(profileImage.getImage());

        ContentValues contentValues = new ContentValues();
        contentValues.put(IMAGE, image);

        return db.update(IMAGES_TABLE_NAME, contentValues, _ID + "= ?",
                new String[]{ profileImage.getProfileId() });
    }
}
