package com.slack.motometer.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import com.slack.motometer.ui.adapters.TaskAdapter;

import java.util.Collections;
import java.util.List;

public class TasksOverview extends AppCompatActivity {

    // UI components
    private ListView taskContainer;
    private TextView profileTitle, profileHoursValue;

    // Logic components
    private ProfileRepository profileRepository;
    private TaskRepository taskRepository;
    private String profileId;
    private Profile profile;
    private List<Task> tasks;
    private ArrayAdapter<Task> taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_overview);

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.tasks_overview_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.toolbar_tasks_overview_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // getExtra - active profile ID
        profileId = getIntent().getExtras().getString("profileId");

        // Fetch active profile
        profileRepository = new ProfileService(this);
        profile = profileRepository.getProfile(Integer.parseInt(profileId));

        // Fetch tasks
        taskRepository = new TaskService(this);
        tasks = taskRepository.getProfileTasks(Integer.parseInt(profileId));

        // Get handle on UI elements
        profileTitle = findViewById(R.id.profile_header_title_tv);
        profileHoursValue = findViewById(R.id.profile_header_hours_value_tv);

        // Set UI element values
        profileTitle.setText(new ProfileLogic(this).getProfileTitle(profile));
        profileHoursValue.setText(profile.getHours());

        // Create and set adapter for ListView
        taskAdapter = new TaskAdapter(this, R.layout.task_card_view, tasks);
        taskContainer = findViewById(R.id.taskListView);
        taskContainer.setAdapter(taskAdapter);

        // Set view to display message when no Task(s) have been created for the active profile
        taskContainer.setEmptyView(findViewById(R.id.empty_task_item));

        // Make ListView items clickable and set listener/handler
        taskContainer.setClickable(true);
        taskContainer.setOnItemClickListener((adapterView, view, i, l) -> {
            // start task sign off activity
            Task task = (Task)taskContainer.getItemAtPosition(i);
            Intent taskSignoffActivity = new Intent(getBaseContext(), TaskSignOff.class);
            taskSignoffActivity.putExtra("taskId", task.getId());
            taskSignoffActivity.putExtra("profileId", profileId);
            startActivityForResult(taskSignoffActivity, 3);
        });
    }

    // Ensure fresh db data fetched on activity resume
    @Override
    protected void onResume() {
        super.onResume();
        // refresh profiles object to display up-to-date listview
        tasks = taskRepository.getProfileTasks(Integer.parseInt(profileId));
        // clear adapter and repopulate with fresh profiles data
        taskAdapter.clear();
        taskAdapter.addAll(tasks);
    }

    // Inflate toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tasks_overview_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Set toolbar icon actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.toolbar_tasks_overview_icon_add:
                // start NewTask activity
                Intent newTaskIntent = new Intent(this, NewTask.class);
                newTaskIntent.putExtra("profileId", profileId);
                startActivity(newTaskIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}