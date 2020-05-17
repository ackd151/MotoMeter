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
        dbHelper = DatabaseHelper.getInstance(context);
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
        String imagePath = cursor.getString(2);
        Bitmap image = null;
        // Scale image for largest view in app (~ 200 x 356)
        BitmapFactory.Options options= new BitmapFactory.Options();
        options.inJustDecodeBounds= true;
        BitmapFactory.decodeFile(imagePath, options);
        int srcWidth = options.outWidth, srcHeight = options.outHeight;
        int scaleFactor = (int)Math.max(1.0, Math.min((double) srcWidth / (double)200,
                (double)srcHeight / (double)356));
        scaleFactor = (int)Math.pow(2.0, Math.floor(Math.log(scaleFactor) / Math.log(2.0)));
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        do {
            try {
                scaleFactor*= 2;
                image= BitmapFactory.decodeFile(imagePath, options);
            } catch(OutOfMemoryError e) {
                options.inSampleSize = scaleFactor;
            }
        }
        while(image == null && scaleFactor <= 256);

        ProfileImage profileImage = new ProfileImage(cursor.getString(0), cursor.getString(1),
                imagePath, image);
        db.close();
        return profileImage;
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

