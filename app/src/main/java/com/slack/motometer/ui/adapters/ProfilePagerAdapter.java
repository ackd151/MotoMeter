package com.slack.motometer.ui.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.ui.fragments.ProfileCard;

import java.util.List;

public class ProfilePagerAdapter extends FragmentPagerAdapter {

    private List<Profile> profiles;

    public ProfilePagerAdapter(@NonNull FragmentManager fm, List<Profile> profiles) {
        super(fm);
        this.profiles = profiles;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return ProfileCard.newInstance(profiles.get(position).getId());
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return profiles.get(position).getModel();
//        return super.getPageTitle(position);
    }

    @Override
    public int getCount() {
        return profiles.size(); //?? wut dis??
    }
}
