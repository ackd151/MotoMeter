package com.slack.motometer.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.slack.motometer.R;
import com.slack.motometer.domain.logic.ProfileLogic;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.model.Task;
import com.slack.motometer.domain.repositories.ProfileRepository;
import com.slack.motometer.domain.repositories.TaskRepository;
import com.slack.motometer.domain.services.ProfileService;
import com.slack.motometer.domain.services.TaskService;

public class EditTask extends AppCompatActivity {

    // UI components
    private EditText nameValue, intervalValue, completedAtValue;

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
        nameValue = findViewById(R.id.edit_task_name_value_et);
        intervalValue = findViewById(R.id.edit_task_interval_value_et);
        completedAtValue = findViewById(R.id.edit_task_completed_at_value_et);

        // Set UI components
        nameValue.setText(task.getTaskTitle());
        nameValue.setSelectAllOnFocus(true);
        intervalValue.setText(String.valueOf(task.getInterval()));
        intervalValue.setSelectAllOnFocus(true);
        completedAtValue.setText(String.valueOf(task.getLastCompletedAt()));
        completedAtValue.setSelectAllOnFocus(true);
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
            // Back button - return to previous activity
            case android.R.id.home:
                finish();
                return true;
            // Save profile edits to db
            case R.id.toolbar_edit_task_icon_save:
                if (validateEditTaskForm()) {
                    saveTaskEdits();
                    finish();
                }
                return true;
            // Clear edit profile fields
            case R.id.toolbar_edit_task_icon_clear:
                nameValue.setText("");
                intervalValue.setText("");
                completedAtValue.setText("");
                nameValue.requestFocus();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Helper method to validate all edittexts in edit profile activity
    private boolean validateEditTaskForm() {
        if (validateEditText(nameValue) && validateEditText(intervalValue)
                && validateEditText(completedAtValue)) {
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
                nameValue.getText().toString(), intervalValue.getText().toString(),
                completedAtValue.getText().toString()));
    }
}
