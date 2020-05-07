package com.slack.motometer.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.slack.motometer.R;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.model.ProfileImage;
import com.slack.motometer.domain.repositories.ImageRepository;
import com.slack.motometer.domain.services.ImageService;

import java.util.List;

public class ProfileAdapter extends ArrayAdapter<Profile> {

    private List<Profile> profiles;

    public ProfileAdapter(Context context, int textViewResourceId, List<Profile> profiles) {
        super(context, textViewResourceId, profiles);
        this.profiles = profiles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater)getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.profile_card_view, null);
        }
        Profile profile = profiles.get(position);
        if (profile != null) {
            TextView profileTitleTV = view.findViewById(R.id.profile_card_view_title_tv);
            TextView hoursTV = view.findViewById(R.id.profile_card_view_hrs_value_tv);
            if (profileTitleTV != null) {
                String profileTitle = profile.getYear() + " " + profile.getMake() + " "
                        + profile.getModel();
                profileTitleTV.setText(profileTitle);
                hoursTV.setText(profile.getHours());
            }
        }
        // Set profile image
        ImageRepository imageRepository = new ImageService(super.getContext());
        ProfileImage profileImage = imageRepository.getImageByProfileId(profile.getId());
        ImageView imageView = view.findViewById(R.id.profile_card_view_iv);
        imageView.setImageBitmap(profileImage.getImage());

        return view;
    }
}
