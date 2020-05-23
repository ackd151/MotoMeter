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

import com.slack.motometer.R;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.model.Task;
import com.slack.motometer.domain.repositories.ProfileRepository;
import com.slack.motometer.domain.repositories.TaskRepository;
import com.slack.motometer.domain.services.ProfileService;
import com.slack.motometer.domain.services.TaskService;

public class EditTask extends AppCompatActivity {

    // UI components
    private EditText nameValueET, intervalValueET, completedAtValueET;
    private TextView infoPanelTV;
    private ConstraintLayout infoPanelCL;

    // Logic components
    private String taskId;
    private TaskRepository taskRepository;
    private ProfileRepository profileRepository;
    private Task task;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        // Set toolbar
        Toolbar myToolbar = findViewById(R.id.edit_task_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.toolbar_edit_task_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set Logic components
        taskId = getIntent().getExtras().getString("taskId");
        taskRepository = new TaskService(this);
        task = taskRepository.getTask(Integer.parseInt(taskId));
        profileRepository = new ProfileService(this);
        profile = profileRepository.getProfile(Integer.parseInt(task.getProfileId()));

        // Get handle on UI components
        nameValueET = findViewById(R.id.edit_task_name_value_et);
        intervalValueET = findViewById(R.id.edit_task_interval_value_et);
        completedAtValueET = findViewById(R.id.edit_task_completed_at_value_et);
        infoPanelTV = findViewById(R.id.info_panel_info_text_tv);
        infoPanelCL = findViewById(R.id.info_panel_cl);

        // Set UI components
        nameValueET.setText(task.getTaskTitle());
        nameValueET.setSelectAllOnFocus(true);
        intervalValueET.setText(String.valueOf(task.getInterval()));
        intervalValueET.setSelectAllOnFocus(true);
        completedAtValueET.setText(String.valueOf(task.getLastCompletedAt()));
        completedAtValueET.setSelectAllOnFocus(true);
        // Set infoPanel
        infoPanelTV.setText(R.string.activity_edit_task_information);
        // Hide info panel again if user clicks help panel
        infoPanelCL.setClickable(true);
        infoPanelCL.setOnClickListener(v -> infoPanelCL.setVisibility(View.GONE));
        // Set edit profile cancel/confirm buttons
        findViewById(R.id.edit_task_confirm_btn).setOnClickListener(v -> {
            // Save profile edits to db
            if (validateEditTaskForm()) {
                saveTaskEdits();
                finish();
            }
        });
        findViewById(R.id.edit_task_cancel_btn).setOnClickListener(v -> finish());
    }

    // Inflate toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_task_menu, menu);
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
            case R.id.toolbar_edit_task_help:
                infoPanelCL.setVisibility(View.VISIBLE);
                return true;
            case R.id.toolbar_edit_task_icon_clear:
                // Clear edit profile fields
                nameValueET.setText("");
                intervalValueET.setText("");
                completedAtValueET.setText("");
                nameValueET.requestFocus();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Helper method to validate all edittexts in edit profile activity
    private boolean validateEditTaskForm() {
        if (validateEditText(nameValueET) && validateEditText(intervalValueET)
                && validateEditText(completedAtValueET)) {
            return true;
        }
        return false;
    }

    // Helper method to ensure edittext field is not empty
    private boolean validateEditText(EditText editText) {
        if (editText.getText().toString().length() < 1) {
            editText.setError(getResources().getString(R.string.validation_entry_required));
            editText.requestFocus();
            return false;
        }
        return true;
    }

    // Helper method to save profile edits to db
    private void saveTaskEdits() {
        taskRepository.updateTask(new Task(task.getId(), task.getProfileId(),
                nameValueET.getText().toString(), intervalValueET.getText().toString(),
                completedAtValueET.getText().toString()));
    }
}
