package com.slack.motometer.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.slack.motometer.R;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.repositories.ProfileRepository;
import com.slack.motometer.domain.services.ProfileService;

public class ProfileOverview extends AppCompatActivity {

    // UI components
    private TextView infoPanelTV;
    private ConstraintLayout infoPanelCL;

    // Logic components
    private ProfileRepository profileRepository;
    private Profile profile;
    private String profileId;

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

        // Set Logic components
        profileId = getIntent().getExtras().getString("profileId");
        profileRepository = new ProfileService(this);
        profile = profileRepository.getProfile(Integer.parseInt(profileId));

        // Set UI components
        // Set infoPanel
        infoPanelCL = findViewById(R.id.info_panel_cl);
        infoPanelTV = findViewById(R.id.info_panel_info_text_tv);
        infoPanelTV.setText(R.string.activity_profile_overview_information);
        // Hide info panel again if user clicks help panel
        infoPanelCL.setClickable(true);
        infoPanelCL.setOnClickListener(v -> infoPanelCL.setVisibility(View.GONE));
        // set startActivity button actions
        setOnClick(R.id.profile_overview_home_btn, MainActivity.class);
        setOnClick(R.id.profile_overview_maintenance_btn, TasksOverview.class);
        setOnClick(R.id.profile_overview_post_ride_btn, PostRide.class);
        setOnClick(R.id.profile_overview_pre_ride_btn, PreRide.class);
        setOnClick(R.id.profile_overview_notes_btn, Notes.class);
        setOnClick(R.id.profile_overview_edit_btn, EditProfile.class);
    }

    // Fetch fresh active profile record to be displayed on resume
    @Override
    protected void onResume() {
        super.onResume();
        profile = profileRepository.getProfile(Integer.parseInt(profileId));
    }

    // Set button onClick to start associated activity.class
    private void setOnClick(int btnId, Class cls) {
        findViewById(btnId).setOnClickListener((view) -> {
            Intent intent = new Intent(this, cls);
            intent.putExtra("profileId", profileId);
            startActivity(intent);
        });
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
            case android.R.id.home:
                // Back button - return to previous activity
                finish();
                return true;
            case R.id.toolbar_profile_overview_help:
                // Show info panel
                infoPanelCL.setVisibility(View.VISIBLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
