package com.slack.motometer.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.slack.motometer.R;
import com.slack.motometer.domain.logic.ProfileLogic;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.model.Task;
import com.slack.motometer.domain.repositories.ProfileRepository;
import com.slack.motometer.domain.repositories.TaskRepository;
import com.slack.motometer.domain.services.ProfileService;
import com.slack.motometer.domain.services.TaskService;
import com.slack.motometer.ui.adapters.TaskAdapter;
import com.slack.motometer.utilities.BottomNavListener;

import java.util.List;

public class TasksOverview extends AppCompatActivity {

    // UI components
    private ListView taskContainerLV;
    private TextView profileTitleTV, profileHoursValueTV, infoPanelTV;
    private BottomNavigationView navBar;
    private ConstraintLayout infoPanelCL;

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

        // Set Logic components
        // getExtra - active profile ID
        profileId = getIntent().getExtras().getString("profileId");

        // Fetch active profile
        profileRepository = new ProfileService(this);
        profile = profileRepository.getProfile(Integer.parseInt(profileId));

        // Fetch tasks
        taskRepository = new TaskService(this);
        tasks = taskRepository.getProfileTasks(Integer.parseInt(profileId));

        // Get handle on UI elements
        profileTitleTV = findViewById(R.id.profile_header_title_tv);
        profileHoursValueTV = findViewById(R.id.profile_header_hours_value_tv);
        infoPanelTV = findViewById(R.id.info_panel_info_text_tv);
        infoPanelCL = findViewById(R.id.info_panel_cl);

        // Set UI element values
        profileTitleTV.setText(new ProfileLogic(this).getProfileTitle(profile));
        profileHoursValueTV.setText(profile.getHours());
        // Set infoPanel
        infoPanelTV.setText(R.string.activity_tasks_overview_information);
        // Hide info panel again if user clicks help panel
        infoPanelCL.setClickable(true);
        infoPanelCL.setOnClickListener(v -> infoPanelCL.setVisibility(View.GONE));

        // Create and set adapter for ListView
        taskAdapter = new TaskAdapter(this, R.layout.task_card_view, tasks);
        taskContainerLV = findViewById(R.id.tasks_overview_tasks_list_view_lv);
        taskContainerLV.setAdapter(taskAdapter);

        // Set view to display message when no Task(s) have been created for the active profile
        taskContainerLV.setEmptyView(findViewById(R.id.empty_task_item));

        // Make ListView items clickable and set listener/handler
        taskContainerLV.setClickable(true);
        taskContainerLV.setOnItemClickListener((adapterView, view, i, l) -> {
            // start task sign off activity
            Task task = (Task) taskContainerLV.getItemAtPosition(i);
            Intent taskSignoffActivity = new Intent(getBaseContext(), TaskSignOff.class);
            taskSignoffActivity.putExtra("taskId", task.getId());
            taskSignoffActivity.putExtra("profileId", profileId);
            startActivityForResult(taskSignoffActivity, 3);
        });

        // Set bottom navigation bar
        navBar = findViewById(R.id.tasks_overview_nav_bar);
        navBar.setSelectedItemId(R.id.bottom_nav_maintenance);
        navBar.setOnNavigationItemSelectedListener(new BottomNavListener(this, profileId));
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
        navBar.getMenu().findItem(R.id.bottom_nav_maintenance).setChecked(true);
    }

    // Inflate toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tasks_overview_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Set toolbar actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Back button - return to previous activity
                finish();
                return true;
            case R.id.toolbar_tasks_overview_help:
                // Show info panel
                infoPanelCL.setVisibility(View.VISIBLE);
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
