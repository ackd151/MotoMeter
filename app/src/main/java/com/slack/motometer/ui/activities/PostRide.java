package com.slack.motometer.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.slack.motometer.R;
import com.slack.motometer.domain.logic.ChecklistLogic;
import com.slack.motometer.domain.logic.ProfileLogic;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.repositories.ProfileRepository;
import com.slack.motometer.domain.services.ProfileService;

public class PostRide extends AppCompatActivity {

    // UI components
    private EditText hoursValue;

    // Logic components
    private ProfileRepository profileRepository;
    private ProfileLogic profileLogic;
    private String profileId;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ride);

        // Set toolbar
        Toolbar myToolbar = findViewById(R.id.post_ride_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.toolbar_post_ride_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Get handle on UI components
        hoursValue = findViewById(R.id.post_ride_hours_value_et);

        // Get active profile
        profileId = getIntent().getExtras().getString("profileId");
        profileRepository = new ProfileService(this);
        profile = profileRepository.getProfile(Integer.parseInt(profileId));

        // Set profile header data
        profileLogic = new ProfileLogic(this);

        // Set button listeners/handlers
        findViewById(R.id.post_ride_confirm_btn).setOnClickListener((view) -> {
            if (validateHoursValue(hoursValue)) {
                // Update profile hours
                profileLogic.postRide(profile, hoursValue.getText().toString());
                // Also reset pre-ride inspections
                new ChecklistLogic(this).resetChecklist(Integer.parseInt(profileId));
                finish();
            }
        });
        findViewById(R.id.post_ride_cancel_btn).setOnClickListener((view) -> {
            finish();
        });

        // Set bottom navigation bar
        BottomNavigationView navBar = findViewById(R.id.post_ride_nav_bar);
        navBar.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.post_ride_nav_home:
                    Intent homeIntent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(homeIntent);
                    return true;
                case R.id.post_ride_nav_maintenance:
                    Intent maintenanceIntent = new Intent(getBaseContext(), TasksOverview.class);
                    maintenanceIntent.putExtra("profileId", profileId);
                    startActivity(maintenanceIntent);
                    return true;
                case R.id.post_ride_nav_pre_ride:
                    Intent preRideIntent = new Intent(getBaseContext(), PreRide.class);
                    preRideIntent.putExtra("profileId", profileId);
                    startActivity(preRideIntent);
                    return true;
                case R.id.post_ride_nav_notes:
                    Intent notesIntent = new Intent(getBaseContext(), Notes.class);
                    notesIntent.putExtra("profileId", profileId);
                    startActivity(notesIntent);
                    return true;
            }
            return false;
        });
    }

    // Set toolbar icon actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Back button - return to previous activity
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Helper method to validate edittext hours value
    private boolean validateHoursValue(EditText editText) {
        // check for empty edittext
        if (editText.getText().toString().length() < 1) {
            editText.setError(getResources().getString(R.string.validation_entry_required));
            editText.requestFocus();
            return false;
        }
        // check new hours greater than current value
        float newHours = Float.parseFloat(editText.getText().toString());
        float currentHours = Float.parseFloat(profile.getHours());
        if (newHours <= currentHours) {
            editText.setError(getResources().getString(R.string.validation_hours_gt_current));
            editText.requestFocus();
            return false;
        }
        return true;
    }


}
