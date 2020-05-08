package com.slack.motometer.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.slack.motometer.R;
import com.slack.motometer.domain.logic.ProfileLogic;
import com.slack.motometer.domain.logic.TaskLogic;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.model.Task;
import com.slack.motometer.domain.repositories.ProfileRepository;
import com.slack.motometer.domain.repositories.TaskRepository;
import com.slack.motometer.domain.services.ProfileService;
import com.slack.motometer.domain.services.TaskService;

public class TaskSignOff extends AppCompatActivity {

    // UI components
    private TextView taskTitle, taskDueIn, taskIntervalValue,
            taskLastAtValue;
    private EditText signOffValue;

    // Logic components
    private ProfileRepository profileRepository;
    private TaskRepository taskRepository;
    private TaskLogic taskLogic;
    private String taskId;
    private Task task;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_sign_off);

        // Set toolbar
        Toolbar myToolbar = findViewById(R.id.task_sign_off_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.toolbar_task_sign_off_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Get extra profileId
        taskId = getIntent().getExtras().getString("taskId");

        // Set Logic components
        taskRepository = new TaskService(this);
        task = taskRepository.getTask(Integer.parseInt(taskId));
        profileRepository = new ProfileService(this);
        profile = profileRepository.getProfile(Integer.parseInt(task.getProfileId()));
        taskLogic = new TaskLogic(this, profile.getId());

        // Get handle on UI components
        taskTitle = findViewById(R.id.task_sign_off_task_title_tv);
        taskDueIn = findViewById(R.id.task_sign_off_value_tv);
        signOffValue = findViewById(R.id.task_sign_off_value_et);
        taskIntervalValue = findViewById(R.id.task_sign_off_interval_value_tv);
        taskLastAtValue = findViewById(R.id.task_sign_off_last_at_value_tv);

        // Set UI components
        taskTitle.setText(task.getTaskTitle());
        taskDueIn.setText(taskLogic.getRemainingHours(task));
        signOffValue.setText(profile.getHours());
        signOffValue.setSelection(signOffValue.getText().length());
        taskIntervalValue.setText(String.valueOf(task.getInterval()));
        taskLastAtValue.setText(String.valueOf(task.getLastCompletedAt()));
        // Set sign off button listener/handler
        findViewById(R.id.task_sign_off_btn).setOnClickListener((view) -> {
            if (validateHours()) {
                float signOffHours = Float.parseFloat(signOffValue.getText().toString());
                taskLogic.signOffTask(task, signOffHours);
                finish();
            }
        });
    }

    // Ensure fresh db data fetched on activity resume
    @Override
    protected void onResume() {
        super.onResume();
        // refresh profiles object to display up-to-date listview
        task = taskRepository.getTask(Integer.parseInt(taskId));
        taskTitle.setText(task.getTaskTitle());
        taskDueIn.setText(taskLogic.getRemainingHours(task));
    }

    // Inflate toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_sign_off_menu, menu);
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
            // Edit task fields
            case R.id.toolbar_task_sign_off_icon_edit:
                Intent intent = new Intent(this, EditTask.class);
                intent.putExtra("taskId", taskId);
                intent.putExtra("profileId", profile.getId());
                startActivity(intent);
                return true;
            // Delete task from db/app
            case R.id.toolbar_task_sign_off_icon_delete:
                AlertDialog taskDeleteDialog = deleteTask(task);
                taskDeleteDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Helper method to build the alert dialog for task deletion
    private AlertDialog deleteTask(Task taskToDelete) {
        return new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.alert_dialog_delete_task_title))
                .setMessage(getResources().getString(R.string.alert_dialog_delete_task_message))
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(getResources().getString(R.string.alert_dialog_confirm),
                        (dialogInterface, i) -> {
                    taskRepository.deleteTask(task);
                    dialogInterface.dismiss();
                    finish();
                })
                .setNegativeButton(getResources().getString(R.string.alert_dialog_cancel),
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .create();
    }

    // Helper method to validate edittext values
    private boolean validateHours() {
        String hoursString = signOffValue.getText().toString();
        if (hoursString.length() < 1) {
            signOffValue.setError(getResources().getString(R.string.validation_entry_required));
            signOffValue.requestFocus();
            return false;
        } else if (Float.parseFloat(hoursString) > Float.parseFloat(profile.getHours())) {
            signOffValue.setError(getResources().getString(R.string.validation_hours_lte_current));
            signOffValue.requestFocus();
            return false;
        }
        return true;
    }
}