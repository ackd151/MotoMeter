package com.slack.motometer.domain.repositories;

import com.slack.motometer.domain.model.ProfileImage;

public interface ImageRepository {

    ProfileImage getImageByProfileId(String profileId);

    int updateImage(ProfileImage profileImage);
}
