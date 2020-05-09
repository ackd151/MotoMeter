package com.slack.motometer.domain.repositories;

import com.slack.motometer.domain.model.ProfileImage;

public interface ImageRepository {

    long addImage(ProfileImage profileImage);

    ProfileImage getImageByProfileId(String profileId);

    void updateImage(ProfileImage profileImage);
}
