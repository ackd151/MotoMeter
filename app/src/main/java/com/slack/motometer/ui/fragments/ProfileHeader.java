package com.slack.motometer.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.slack.motometer.R;
import com.slack.motometer.domain.logic.ProfileLogic;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.model.ProfileImage;
import com.slack.motometer.domain.repositories.ImageRepository;
import com.slack.motometer.domain.repositories.ProfileRepository;
import com.slack.motometer.domain.services.ImageService;
import com.slack.motometer.domain.services.ProfileService;

public class ProfileHeader extends Fragment {

    // UI components
    private TextView profileHeaderTitleTV, profileHeaderHoursValueTV;
    private ImageView profileImageIV;

    // Logic components
    private Context context;
    private ProfileRepository profileRepository;
    private Profile profile;
    private String profileId;
    private ImageRepository imageRepository;
    private ProfileImage profileImage;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_header, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Get handle on UI components
        profileHeaderTitleTV = view.findViewById(R.id.profile_header_title_tv);
        profileHeaderHoursValueTV = view.findViewById(R.id.profile_header_hours_value_tv);
        profileImageIV = view.findViewById(R.id.profile_header_img_iv);

        // Set Logic components
        profileId = getActivity().getIntent().getExtras().getString("profileId");
        profileRepository = new ProfileService(context);
        profile = profileRepository.getProfile(Integer.parseInt(profileId));
        imageRepository = new ImageService(context);
        profileImage = imageRepository.getImageByProfileId(profileId);

        // Set UI components
        profileHeaderTitleTV.setText(new ProfileLogic(context).getProfileTitle(profile));
        profileHeaderHoursValueTV.setText(profile.getHours());
        profileImageIV.setImageBitmap(profileImage.getImage());
    }
}
