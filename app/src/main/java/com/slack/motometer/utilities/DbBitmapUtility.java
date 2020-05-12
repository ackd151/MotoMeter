package com.slack.motometer.utilities;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class DbBitmapUtility {

    // Convert bitmap to byte[]
    public static byte[] getByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
