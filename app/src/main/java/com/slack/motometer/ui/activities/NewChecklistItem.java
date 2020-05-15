package com.slack.motometer.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.slack.motometer.R;
import com.slack.motometer.domain.model.ChecklistItem;
import com.slack.motometer.domain.repositories.ChecklistRepository;
import com.slack.motometer.domain.services.ChecklistService;

public class NewChecklistItem extends AppCompatActivity {

    // UI components
    private EditText checkListItemTitleValueET;
    private TextView infoPanelTV;

    // Logic components
    private String profileId;
    private ChecklistRepository checklistRepository;

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

        // Get handle on UI components
        checkListItemTitleValueET = findViewById(R.id.new_cl_item_title_value_et);
        infoPanelTV = findViewById(R.id.information_tv);

        // Set UI components
        // Request focus for edittext
        checkListItemTitleValueET.requestFocus();
        infoPanelTV.setText(R.string.activity_new_checklist_item_information);
        // Set confirm/cancel button onClick listeners/handlers
        findViewById(R.id.new_cl_item_confirm_btn).setOnClickListener(view -> {
            if (validateClItemTitleValue(checkListItemTitleValueET)) {
                // Create new checklist item and record in db
                checklistRepository.addChecklistItem(
                        new ChecklistItem(profileId, checkListItemTitleValueET.getText().toString()));
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
