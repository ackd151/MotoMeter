package com.slack.motometer.domain.model;

import android.graphics.Bitmap;

public class ProfileImage {

    private String id, profileId;
    private Bitmap image;

    public ProfileImage(String id, String profileId, Bitmap image) {
        this.id = id;
        this.profileId = profileId;
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

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
