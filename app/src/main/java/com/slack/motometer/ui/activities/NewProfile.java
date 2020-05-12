package com.slack.motometer.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.slack.motometer.R;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.repositories.ProfileRepository;
import com.slack.motometer.domain.services.ProfileService;

public class NewProfile extends AppCompatActivity {

    // UI components
    private EditText yearET, makeET, modelET, hoursET;

    // Logic components
    private ProfileRepository profileRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);

        // Set toolbar
        Toolbar myToolbar = findViewById(R.id.new_profile_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.toolbar_new_profile_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set UI components
        yearET = findViewById(R.id.new_profile_year_et);
        makeET = findViewById(R.id.new_profile_make_et);
        modelET = findViewById(R.id.new_profile_model_et);
        hoursET = findViewById(R.id.new_profile_hourmeter_et);
        // Set focus on first edittext
        yearET.requestFocus();
        // Set confirm/cancel button listeners/handlers
        findViewById(R.id.new_profile_confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // validate editText fields before calling createProfile - not empty only requirement
                if (editTextNotEmpty(yearET) && editTextNotEmpty(makeET) &&
                        editTextNotEmpty(modelET) && editTextNotEmpty(hoursET)) {
                    createProfile(view);
                    finish();
                }
            }
        });
        findViewById(R.id.new_profile_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Set Logic components
        profileRepository = new ProfileService(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.toolbar_new_profile_icon_clear:
                yearET.setText("");
                makeET.setText("");
                modelET.setText("");
                hoursET.setText("");
                yearET.requestFocus();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Create new profile and record in db
    private void createProfile(View view) {
        String yearValue = yearET.getText().toString();
        String makeValue = makeET.getText().toString();
        String modelValue = modelET.getText().toString();
        String hourmeterValue = hoursET.getText().toString();
        profileRepository.addProfile(new Profile(yearValue, makeValue, modelValue, hourmeterValue));
    }

    // Helper method to validate user input in edittext fields
    private boolean editTextNotEmpty(EditText editText) {
        String editTextValue = editText.getText().toString();
        if (TextUtils.isEmpty(editTextValue)) {
            editText.setError(getResources().getString(R.string.validation_entry_required));
            editText.requestFocus();
            return false;
        }
        return true;
    }
}
