package com.slack.motometer.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

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
    private ListView checklistItemContainerLV;
    private TextView readyNotReadyTV, infoPanelTV;
    private Toolbar toolbar;
    private MenuItem delete, deleteAll;
    private ConstraintLayout infoPanelCL;

    // Logic components
    private ChecklistRepository checklistRepository;
    private String profileId;
    private List<ChecklistItem> checklistItems;
    private ArrayAdapter<ChecklistItem> checklistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_ride);

        // getExtra - active profile ID
        profileId = getIntent().getExtras().getString("profileId");

        // Fetch checklist items
        checklistRepository = new ChecklistService(this);
        checklistItems = checklistRepository.getProfileChecklistItems(Integer.parseInt(profileId));

        // Get handle on UI components
        checklistItemContainerLV = findViewById(R.id.pre_ride_cl_lv);
        readyNotReadyTV = findViewById(R.id.pre_ride_ready_tv);
        infoPanelTV = findViewById(R.id.info_panel_info_text_tv);
        infoPanelCL = findViewById(R.id.info_panel_cl);

        // Set UI components
        // Set ready-not-ready textview text and color depending on checklist completion status
        isChecklistComplete();
        // Set infoPanel
        infoPanelTV.setText(R.string.activity_pre_ride_information);
        // Hide info panel again if user clicks help panel
        infoPanelCL.setClickable(true);
        infoPanelCL.setOnClickListener(v -> infoPanelCL.setVisibility(View.GONE));

        // Create and set adapter for ListView
        checklistAdapter = new ChecklistAdapter(this, R.layout.checklist_card_view,
                checklistItems, this);
        checklistItemContainerLV = findViewById(R.id.pre_ride_cl_lv);
        checklistItemContainerLV.setAdapter(checklistAdapter);

        // Set view to display message when no Task(s) have been created for the active profile
        checklistItemContainerLV.setEmptyView(findViewById(R.id.empty_checklist_item));

        // Set toolbar
        toolbar = findViewById(R.id.pre_ride_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.toolbar_pre_ride_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        // Set bottom navigation bar
        BottomNavigationView navBar = findViewById(R.id.pre_ride_nav_bar);
        navBar.setSelectedItemId(R.id.bottom_nav_pre_ride);
        navBar.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_nav_home:
                    Intent homeIntent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(homeIntent);
                    return true;
                case R.id.bottom_nav_maintenance:
                    Intent maintenanceIntent = new Intent(getBaseContext(), TasksOverview.class);
                    maintenanceIntent.putExtra("profileId", profileId);
                    startActivity(maintenanceIntent);
                    return true;
                case R.id.bottom_nav_post_ride:
                    Intent postRideIntent = new Intent(getBaseContext(), PostRide.class);
                    postRideIntent.putExtra("profileId", profileId);
                    startActivity(postRideIntent);
                    return true;
                case R.id.bottom_nav_pre_ride:
                    Intent preRideIntent = new Intent(getBaseContext(), PreRide.class);
                    preRideIntent.putExtra("profileId", profileId);
                    startActivity(preRideIntent);
                    return true;
                case R.id.bottom_nav_notes:
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        delete = menu.findItem(R.id.toolbar_pre_ride_delete_cl_items);
        deleteAll = menu.findItem(R.id.toolbar_pre_ride_delete_all_cl_items);
        // Set toolbar option visibility
        if (checklistItems.size() == 0) {
            hideToolbarMenuItems(delete, deleteAll);
        } else {
            showToolbarMenuItems(delete, deleteAll);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    // Set toolbar icon actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Back button - return to previous activity
                finish();
                return true;
            case R.id.toolbar_pre_ride_help:
                // Show info panel
                infoPanelCL.setVisibility(View.VISIBLE);
                return true;
            case R.id.toolbar_pre_ride_add_cl_item:
                // Start NewChecklistItem activity
                Intent intent = new Intent(this, NewChecklistItem.class);
                intent.putExtra("profileId", profileId);
                startActivityForResult(intent, 1);
                return true;
            case R.id.toolbar_pre_ride_delete_cl_items:
                // Make cLItem delete buttons visible
                for (int i = 0; i < checklistItemContainerLV.getCount() ; ++i) {
                    View view = getViewFromListView(i, checklistItemContainerLV);
                    Button deleteClItemBtn = view.findViewById(R.id.checklist_card_delete_btn);
                    deleteClItemBtn.setVisibility(View.VISIBLE);
                    int finalI = i;
                    deleteClItemBtn.setOnClickListener(v -> {
                        checklistRepository.deleteChecklistItem(checklistItems.get(finalI));
                        onResume(); // Get fresh data after deletion
                        isChecklistComplete();
                    });
                }
                return true;
            case R.id.toolbar_pre_ride_delete_all_cl_items:
                // Delete all cLItems
                AlertDialog deleteAllClItemsDialog = createDeleteAllDialog();
                deleteAllClItemsDialog.show();
                return true;
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
        checklistItemContainerLV.setAdapter(checklistAdapter);
        isChecklistComplete();
        invalidateOptionsMenu();
    }

    // Used when returning from adding new inspection task, to set readyNotReady textview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1  && resultCode  == RESULT_OK) {
            onResume();
        }
    }

    // Sets readyNotReady textview text and color
    @Override
    public void isChecklistComplete() {
        // Fetch fresh checklistItems from db
        checklistItems = checklistRepository.getProfileChecklistItems(Integer.parseInt(profileId));
        // Set readyNotReady TextView text and color according to checklist completion status
        if (checklistItems.size() == 0) {
            readyNotReadyTV.setVisibility(View.GONE);
        } else {
            readyNotReadyTV.setVisibility(View.VISIBLE);
            boolean checklistComplete = new ChecklistLogic(this).isReady(checklistItems);
            readyNotReadyTV.setText(checklistComplete ? R.string.activity_pre_ride_ready :
                    R.string.activity_pre_ride_not_ready);
            readyNotReadyTV.setBackgroundColor(checklistComplete ?
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

    // Create dialog for delete all action
    private AlertDialog createDeleteAllDialog() {
        return new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.alert_dialog_delete_all_clItems_title))
                .setMessage(getResources().getString(R.string.alert_dialog_delete_all_clItems_message))
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(getResources().getString(R.string.alert_dialog_confirm),
                        (dialogInterface, i) -> {
                            checklistRepository.deleteAllChecklistItems(checklistItems);
                            dialogInterface.dismiss();
                            onResume();
                        })
                .setNegativeButton(getResources().getString(R.string.alert_dialog_cancel),
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .create();
    }

    // Hide toolbar option(s)
    private void hideToolbarMenuItems(MenuItem... items) {
        for (MenuItem item : items) {
            item.setVisible(false);
        }
    }

    // Show toolbar option(s)
    private void showToolbarMenuItems(MenuItem... items) {
        for (MenuItem item : items) {
            item.setVisible(true);
        }
    }
}
