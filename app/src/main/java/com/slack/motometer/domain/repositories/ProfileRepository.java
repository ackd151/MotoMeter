package com.slack.motometer.domain.repositories;

import com.slack.motometer.domain.model.Profile;

import java.util.List;

// Interface for Profile database actions - implementation in services package
public interface ProfileRepository {

    void addProfile(Profile profile);

    Profile getProfile(int id);

    List<Profile> getAllProfiles();

    int updateProfile(Profile profile);

    void deleteProfile(Profile profile);

}
