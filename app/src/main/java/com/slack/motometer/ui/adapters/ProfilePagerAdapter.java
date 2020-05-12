package com.slack.motometer.ui.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.slack.motometer.domain.logic.ProfileLogic;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.ui.fragments.ProfileCard;

import java.util.List;

public class ProfilePagerAdapter extends FragmentStatePagerAdapter {

    private List<Profile> profiles;
    private Context context;

    public ProfilePagerAdapter(@NonNull FragmentManager fm, List<Profile> profiles, Context context) {
        super(fm);
        this.profiles = profiles;
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return ProfileCard.newInstance(profiles.get(position).getId());
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return new ProfileLogic(context).getTabTitle(profiles.get(position));
    }

    @Override
    public int getCount() {
        return profiles.size();
    }
}
