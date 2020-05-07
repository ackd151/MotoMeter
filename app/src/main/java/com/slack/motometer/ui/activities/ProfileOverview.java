package com.slack.motometer.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.slack.motometer.ui.fragments.ProfileHeader;

public class ProfileOverview extends AppCompatActivity {

    // UI components
//    private TextView profileHeaderTitleTV, profileHeaderHoursValueTV;
//    private ImageView profileImageIV;

    // Logic components
    private ProfileRepository profileRepository;
    private Profile profile;
    private String profileId;
    private ImageRepository imageRepository;
    private ProfileImage profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_overview);

        // Set toolbar
        Toolbar myToolbar = findViewById(R.id.profile_overview_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.toolbar_profile_overview_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Get handle on UI components
//        profileHeaderTitleTV = findViewById(R.id.profile_header_title_tv);
//        profileHeaderHoursValueTV = findViewById(R.id.profile_header_hours_value_tv);
//        profileImageIV = findViewById(R.id.profile_header_img_iv);

        // Set Logic components
        profileId = getIntent().getExtras().getString("profileId");
        profileRepository = new ProfileService(this);
        profile = profileRepository.getProfile(Integer.parseInt(profileId));
        imageRepository = new ImageService(this);
        profileImage = imageRepository.getImageByProfileId(profileId);

        // Set UI components
//        profileHeaderTitleTV.setText(new ProfileLogic(this).getProfileTitle(profile));
//        profileHeaderHoursValueTV.setText(profile.getHours());
//        profileImageIV.setImageBitmap(profileImage.getImage());

        // set startActivity button actions
        setOnClick(R.id.profile_overview_maintenance_btn, TasksOverview.class);
        setOnClick(R.id.profile_overview_post_ride_btn, PostRide.class);
        setOnClick(R.id.profile_overview_pre_ride_btn, PreRide.class);
        setOnClick(R.id.profile_overview_notes_btn, Notes.class);
    }

    // Fetch fresh active profile record to be displayed on resume
    @Override
    protected void onResume() {
        super.onResume();
        profile = profileRepository.getProfile(Integer.parseInt(profileId));
        // Only potentially changed value is profile hours
//        profileHeaderHoursValueTV.setText(profile.getHours());
    }

    // Inflate toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_overview_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Set toolbar icon actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Back button - return to previous activity
            case android.R.id.home:
                finish();
                return true;
            // Edit profile fields
            case R.id.toolbar_profile_overview_icon_edit:
                Intent editProfileIntent = new Intent(this, EditProfile.class);
                editProfileIntent.putExtra("profileId", profileId);
                startActivity(editProfileIntent);
                return true;
            // Delete profile from db/app
            case R.id.toolbar_profile_overview_icon_delete:
                AlertDialog profileDeleteDialog = deleteProfile(profile);
                profileDeleteDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // helper method to set button onClick to start associated activity.class
    private void setOnClick(int btnId, Class cls) {
        findViewById(btnId).setOnClickListener((view) -> {
            Intent intent = new Intent(this, cls);
            intent.putExtra("profileId", profileId);
            startActivity(intent);
        });
    }

    private AlertDialog deleteProfile(Profile profileToDelete) {
        return new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.alert_dialog_delete_profile_title))
                .setMessage(getResources().getString(R.string.alert_dialog_delete_profile_message))
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(getResources().getString(R.string.alert_dialog_confirm),
                        (dialogInterface, i) -> {
                    profileRepository.deleteProfile(profile);
                    dialogInterface.dismiss();
                    finish();
                })
                .setNegativeButton(getResources().getString(R.string.alert_dialog_cancel),
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .create();
    }
}
