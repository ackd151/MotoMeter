package com.slack.motometer.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.slack.motometer.R;
import com.slack.motometer.domain.logic.ChecklistLogic;
import com.slack.motometer.domain.model.ChecklistItem;
import com.slack.motometer.domain.repositories.ChecklistRepository;
import com.slack.motometer.domain.services.ChecklistService;
import com.slack.motometer.utilities.ChecklistListener;
import com.slack.motometer.ui.adapters.ChecklistAdapter;

import java.util.List;

public class PreRide extends AppCompatActivity implements ChecklistListener {

    // UI components
    private ListView checklistItemContainer;
    private TextView readyNotReady;

    // Logic components
    private ChecklistRepository checklistRepository;
    private String profileId;
    private List<ChecklistItem> checklistItems;
    private ArrayAdapter<ChecklistItem> checklistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_ride);

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.pre_ride_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.toolbar_pre_ride_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // getExtra - active profile ID
        profileId = getIntent().getExtras().getString("profileId");

        // Fetch checklist items
        checklistRepository = new ChecklistService(this);
        checklistItems = checklistRepository.getProfileChecklistItems(Integer.parseInt(profileId));

        // Get handle on UI components
        checklistItemContainer = findViewById(R.id.pre_ride_cl_lv);
        readyNotReady = findViewById(R.id.pre_ride_ready_tv);

        // Set UI element values
        // Set ready-not-ready textview text and color depending on checklist completion status
        isChecklistComplete();

        // Create and set adapter for ListView
        checklistAdapter = new ChecklistAdapter(this, R.layout.checklist_card_view,
                checklistItems, this);
        checklistItemContainer = findViewById(R.id.pre_ride_cl_lv);
        checklistItemContainer.setAdapter(checklistAdapter);

        // Set view to display message when no Task(s) have been created for the active profile
        checklistItemContainer.setEmptyView(findViewById(R.id.empty_checklist_item));

        // Set bottom navigation bar
        BottomNavigationView navBar = findViewById(R.id.pre_ride_nav_bar);
        navBar.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.pre_ride_nav_home:
                    Intent homeIntent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(homeIntent);
                    return true;
                case R.id.pre_ride_nav_maintenance:
                    Intent maintenanceIntent = new Intent(getBaseContext(), TasksOverview.class);
                    maintenanceIntent.putExtra("profileId", profileId);
                    startActivity(maintenanceIntent);
                    return true;
                case R.id.pre_ride_nav_post_ride:
                    Intent postRideIntent = new Intent(getBaseContext(), PostRide.class);
                    postRideIntent.putExtra("profileId", profileId);
                    startActivity(postRideIntent);
                    return true;
                case R.id.pre_ride_nav_notes:
                    Intent notesIntent = new Intent(getBaseContext(), Notes.class);
                    notesIntent.putExtra("profileId", profileId);
                    startActivity(notesIntent);
                    return true;
            }
            return false;
        });
    }

    // Inflate toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pre_ride_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Set toolbar icon actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.toolbar_pre_ride_add_cl_item:
                Intent intent = new Intent(this, NewChecklistItem.class);
                intent.putExtra("profileId", profileId);
                startActivityForResult(intent, 1);
                return true;
            case R.id.toolbar_pre_ride_delete_cl_items:
                for (int i = 0 ; i < checklistItemContainer.getCount() ; ++i) {
                    View view = getViewFromListView(i, checklistItemContainer);
                    Button deleteClItemBtn = view.findViewById(R.id.checklist_card_delete_btn);
                    deleteClItemBtn.setVisibility(View.VISIBLE);
                    int finalI = i;
                    deleteClItemBtn.setOnClickListener(view1 -> {
                        checklistRepository.deleteChecklistItem(checklistItems.get(finalI));
                        onResume(); // Get fresh data after deletion
                        isChecklistComplete();
                    });
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // On activity resume, ensure fresh data is fetched from db to populate ui views
    @Override
    protected void onResume() {
        super.onResume();
        // refresh checklistItems object to display up-to-date listview
        checklistItems = checklistRepository.getProfileChecklistItems(Integer.parseInt(profileId));
        // clear adapter and repopulate with fresh profiles data
        // adapter.clear() -> addAll() not refreshing the views properly
        // i.e. delete icon staying visible on views outside of ListView port
        // instantiating new adapter with fresh db data instead - appears to fix issue
        checklistAdapter = new ChecklistAdapter(this, R.layout.checklist_card_view,
                checklistItems, this);
        checklistItemContainer.setAdapter(checklistAdapter);
    }

    // Used when returning from adding new inspection task, to set readyNotReady textview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1  && resultCode  == RESULT_OK) {
            isChecklistComplete();
        }
    }

    // Sets readyNotReady textview text and color
    @Override
    public void isChecklistComplete() {
        // Fetch fresh checklistItems from db
        checklistItems = checklistRepository.getProfileChecklistItems(Integer.parseInt(profileId));
        // Set readyNotReady textview text and color according to checklist completion status
        if (checklistItems.size() == 0) {
            readyNotReady.setVisibility(View.GONE);
        } else {
            readyNotReady.setVisibility(View.VISIBLE);
            boolean checklistComplete = new ChecklistLogic(this).isReady(checklistItems);
            readyNotReady.setText(checklistComplete ? R.string.activity_pre_ride_ready :
                    R.string.activity_pre_ride_not_ready);
            readyNotReady.setBackgroundColor(checklistComplete ?
                    getResources().getColor(R.color.accent_pressed) :
                    getResources().getColor(R.color.danger));
        }
    }

    // Helper method to get a listview view by index
    private View getViewFromListView(int index, ListView listView) {
        int firstIndex = listView.getFirstVisiblePosition();
        int lastIndex = firstIndex + listView.getChildCount() - 1;

        if (index < firstIndex || index > lastIndex ) {
            return listView.getAdapter().getView(index, listView.getChildAt(index), listView);
        } else {
            int childIndex = index - firstIndex;
            return listView.getChildAt(childIndex);
        }
    }
}
