package com.slack.motometer.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.slack.motometer.R;
import com.slack.motometer.domain.logic.ProfileLogic;
import com.slack.motometer.domain.model.ChecklistItem;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.repositories.ChecklistRepository;
import com.slack.motometer.domain.repositories.ProfileRepository;
import com.slack.motometer.domain.services.ChecklistService;
import com.slack.motometer.domain.services.ProfileService;

public class NewChecklistItem extends AppCompatActivity {

    // UI components
    private EditText checkListItemTitleValue;
    private TextView profileTitleTV, profileHoursTV;

    // Logic components
    private String profileId;
    private ChecklistRepository checklistRepository;
    private ProfileRepository profileRepository;
    private Profile profile;
    private ProfileLogic profileLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_checklist_item);

        // Set toolbar
        Toolbar myToolbar = findViewById(R.id.new_checklist_item_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.toolbar_new_checklist_item_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set Logic components
        profileId = getIntent().getExtras().getString("profileId");
        checklistRepository = new ChecklistService(this);
        profileRepository = new ProfileService(this);
        profile = profileRepository.getProfile(Integer.parseInt(profileId));
        profileLogic = new ProfileLogic(this);

        // Get handle on UI components
        profileTitleTV = findViewById(R.id.profile_header_title_tv);
        profileHoursTV = findViewById(R.id.profile_header_hours_value_tv);
        checkListItemTitleValue = findViewById(R.id.new_cl_item_title_value_et);

        // Set UI components
        profileTitleTV.setText(profileLogic.getProfileTitle(profile));
        profileHoursTV.setText(profile.getHours());
        // Request focus for edittext
        checkListItemTitleValue.requestFocus();
        // Set confirm/cancel button onClick listeners/handlers
        findViewById(R.id.new_cl_item_confirm_btn).setOnClickListener(view -> {
            if (validateClItemTitleValue(checkListItemTitleValue)) {
                // Create new checklist item and record in db
                checklistRepository.addChecklistItem(
                        new ChecklistItem(profileId, checkListItemTitleValue.getText().toString()));
                setResult(RESULT_OK);
                finish();
            }
        });
        findViewById(R.id.new_cl_item_cancel_btn).setOnClickListener(view -> finish());
    }

    // Helper method to validate checklist item title value
    private boolean validateClItemTitleValue(EditText editText) {
        if (editText.getText().toString().length() < 1) {
            editText.setError(getResources().getString(R.string.validation_entry_required));
            editText.requestFocus();
            return false;
        }
        return true;
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
}
