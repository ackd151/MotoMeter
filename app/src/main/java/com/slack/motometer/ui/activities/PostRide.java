package com.slack.motometer.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.slack.motometer.R;
import com.slack.motometer.domain.logic.ChecklistLogic;
import com.slack.motometer.domain.logic.ProfileLogic;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.repositories.ProfileRepository;
import com.slack.motometer.domain.services.ProfileService;
import com.slack.motometer.utilities.BottomNavListener;

public class PostRide extends AppCompatActivity {

    // UI components
    private EditText hoursValueET;
    private TextView infoPanelTV;
    private ConstraintLayout infoPanelCL;
    private BottomNavigationView navBar;

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
        hoursValueET = findViewById(R.id.post_ride_hours_value_et);
        infoPanelTV = findViewById(R.id.info_panel_info_text_tv);
        infoPanelCL = findViewById(R.id.info_panel_cl);

        // Set Logic components
        profileId = getIntent().getExtras().getString("profileId");
        profileRepository = new ProfileService(this);
        profile = profileRepository.getProfile(Integer.parseInt(profileId));
        profileLogic = new ProfileLogic(this);

        // Set UI components
        // Set infoPanel
        infoPanelTV.setText(R.string.activity_post_ride_information);
        // Hide info panel again if user clicks help panel
        infoPanelCL.setClickable(true);
        infoPanelCL.setOnClickListener(v -> infoPanelCL.setVisibility(View.GONE));
        // Set button listeners/handlers
        findViewById(R.id.post_ride_confirm_btn).setOnClickListener((view) -> {
            if (validateHoursValue(hoursValueET)) {
                // Update profile hours
                profileLogic.postRide(profile, hoursValueET.getText().toString());
                // Also reset pre-ride inspections
                new ChecklistLogic(this).resetChecklist(Integer.parseInt(profileId));
                finish();
            }
        });
        findViewById(R.id.post_ride_cancel_btn).setOnClickListener((view) -> {
            finish();
        });

        // Set bottom navigation bar
        navBar = findViewById(R.id.post_ride_nav_bar);
        navBar.setSelectedItemId(R.id.bottom_nav_post_ride);
        navBar.setOnNavigationItemSelectedListener(new BottomNavListener(this, profileId));
    }

    @Override
    protected void onResume() {
        super.onResume();
        navBar.getMenu().findItem(R.id.bottom_nav_post_ride).setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_ride_menu, menu);
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
            case R.id.toolbar_post_ride_help:
                // Show info panel
                infoPanelCL.setVisibility(View.VISIBLE);
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
