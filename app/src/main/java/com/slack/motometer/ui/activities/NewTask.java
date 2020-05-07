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
import android.widget.TextView;

import com.slack.motometer.R;
import com.slack.motometer.domain.logic.ProfileLogic;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.model.Task;
import com.slack.motometer.domain.repositories.ProfileRepository;
import com.slack.motometer.domain.repositories.TaskRepository;
import com.slack.motometer.domain.services.ProfileService;
import com.slack.motometer.domain.services.TaskService;

public class NewTask extends AppCompatActivity {

    // UI components
    private EditText taskNameET, intervalET, lastCompletedAtET;

    // Logic components
    private String profileId;
    private ProfileRepository profileRepository;
    private TaskRepository taskRepository;
    private Profile profile;
    private ProfileLogic profileLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.new_task_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.toolbar_new_task_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Get extra profileId
        profileId = getIntent().getExtras().getString("profileId");

        // Set logic components
        profileRepository = new ProfileService(this);
        taskRepository = new TaskService(this);
        profile = profileRepository.getProfile(Integer.parseInt(profileId));
        profileLogic = new ProfileLogic(this);

        // Get handle on UI components
        taskNameET = findViewById(R.id.new_task_name_value_et);
        intervalET = findViewById(R.id.new_task_interval_value_et);
        lastCompletedAtET = findViewById(R.id.new_task_last_completed_at_value_et);

        // Set UI components
        // Set confirm/cancel button onClick listeners/handlers
        findViewById(R.id.new_task_confirm_btn).setOnClickListener(view -> {
            if (editTextNotEmpty(taskNameET)
                    && editTextNotEmpty(intervalET)
                    && editTextNotEmpty(lastCompletedAtET)
                    && lastCompletedAtIsValid(lastCompletedAtET)) {
                createTask(view);
            }
        });
        findViewById(R.id.new_task_cancel_btn).setOnClickListener(view -> finish());
        // Give top edittext focus
        taskNameET.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_task_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.toolbar_new_task_clear:
                taskNameET.setText("");
                intervalET.setText("");
                lastCompletedAtET.setText("");
                taskNameET.requestFocus();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Create new maintenance task and record in db
    private void createTask(View view) {
        String taskName = taskNameET.getText().toString();
        String interval = intervalET.getText().toString();
        String lastCompletedAt = lastCompletedAtET.getText().toString();

        Task task = new Task(profileId, taskName, interval, lastCompletedAt);

        taskRepository.addTask(task);
        finish();
    }

    // helper method to validate lastCompletedAt time is less than current engine hours
    private boolean lastCompletedAtIsValid(EditText editText) {
        float currentHours = Float.parseFloat(profileRepository
                .getProfile(Integer.parseInt(profileId)).getHours());
        if (Float.parseFloat(editText.getText().toString()) > currentHours) {
            editText.setError(getResources().getString(R.string.validation_hours_lte_current));
            editText.requestFocus();
            return false;
        }
        return true;
    }

    // helper method to validate user input in edittext fields
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
