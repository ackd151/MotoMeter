package com.slack.motometer.domain.repositories;

import com.slack.motometer.domain.model.Profile;

import java.util.List;

// Interface for Profile database actions - implementation in services package
public interface ProfileRepository {

    long addProfile(Profile profile);

    Profile getProfile(int id);

    List<Profile> getAllProfiles();

    long updateProfile(Profile profile);

    void deleteProfile(Profile profile);

}
