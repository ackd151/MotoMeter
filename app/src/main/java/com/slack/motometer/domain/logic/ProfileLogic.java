package com.slack.motometer.domain.logic;

import android.content.Context;

import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.repositories.ProfileRepository;
import com.slack.motometer.domain.services.ChecklistService;
import com.slack.motometer.domain.services.ProfileService;

public class ProfileLogic {

    private ProfileRepository profileRepository;
    private Context context;

    public ProfileLogic(Context context) {
        this.context = context;
        profileRepository = new ProfileService(context);
    }

    // Creates and returns profile title from profile.year,make,model
    public String getProfileTitle(Profile profile) {
        return profile.getYear() + " " + profile.getMake() + " " + profile.getModel();
    }

    // Post ride updates profile hourmeter value
    public void postRide(Profile profile, String newHours) {
        profile.setHours(newHours);
        profileRepository.updateProfile(profile);
    }
}
