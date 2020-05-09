package com.slack.motometer.domain.model;

import android.graphics.Bitmap;

public class ProfileImage {

    private String id, profileId, imagePath;
    private Bitmap image;

    public ProfileImage(String id, String profileId, String imagePath, Bitmap image) {
        this.id = id;
        this.profileId = profileId;
        this.imagePath = imagePath;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
