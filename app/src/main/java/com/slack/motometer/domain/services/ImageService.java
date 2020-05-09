package com.slack.motometer.domain.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.slack.motometer.domain.db.DatabaseHelper;
import com.slack.motometer.domain.model.ProfileImage;
import com.slack.motometer.domain.repositories.ImageRepository;
import com.slack.motometer.utilities.DbBitmapUtility;

import java.io.File;
import java.io.FileOutputStream;

import static com.slack.motometer.domain.db.DatabaseHelper.IMAGES_TABLE_NAME;
import static com.slack.motometer.domain.db.DatabaseHelper.IMAGE_PATH;
import static com.slack.motometer.domain.db.DatabaseHelper.PROFILE_ID;
import static com.slack.motometer.domain.db.DatabaseHelper._ID;

public class ImageService implements ImageRepository {

    private DatabaseHelper dbHelper;
    private Context context;

    public ImageService(Context context) {
        dbHelper = new DatabaseHelper(context);
        this.context = context;
    }

    // Used to create new profile image record in db
    @Override
    public long addImage(ProfileImage profileImage) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        File deviceStorage = context.getDir("ProfileImages", Context.MODE_PRIVATE);
        File imageFile = new File(deviceStorage, profileImage.getProfileId() + ".jpeg");
        String imagePath = imageFile.toString();

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(imageFile);
            profileImage.getImage().compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.write(DbBitmapUtility.getByteArray(profileImage.getImage()));
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ContentValues contentValues = new ContentValues();

        contentValues.put(PROFILE_ID, profileImage.getProfileId());
        contentValues.put(IMAGE_PATH, imagePath);

        long imageId = db.insert(IMAGES_TABLE_NAME, null, contentValues);
        db.close();

        return imageId;
    }

    @Override
    public ProfileImage getImageByProfileId(String profileId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(IMAGES_TABLE_NAME, new String[]{_ID, PROFILE_ID, IMAGE_PATH},
                PROFILE_ID + "= ?", new String[]{ profileId },
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Bitmap image = BitmapFactory.decodeFile(cursor.getString(2));

        return new ProfileImage(cursor.getString(0), cursor.getString(1),
                cursor.getString(2), image);
    }

    // Overwrite filepath "{profileId}.jpeg" with new image byte[]
    @Override
    public void updateImage(ProfileImage profileImage) {
        File deviceStorage = context.getDir("ProfileImages", Context.MODE_PRIVATE);
        File imageFile = new File(deviceStorage, profileImage.getProfileId() + ".jpeg");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(imageFile);
            profileImage.getImage().compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.write(DbBitmapUtility.getByteArray(profileImage.getImage()));
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

