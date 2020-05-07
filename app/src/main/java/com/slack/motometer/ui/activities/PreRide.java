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

import com.slack.motometer.R;
import com.slack.motometer.domain.logic.ChecklistLogic;
import com.slack.motometer.domain.logic.ProfileLogic;
import com.slack.motometer.domain.model.ChecklistItem;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.repositories.ChecklistRepository;
import com.slack.motometer.domain.repositories.ProfileRepository;
import com.slack.motometer.domain.services.ChecklistService;
import com.slack.motometer.domain.services.ProfileService;
import com.slack.motometer.utilities.ChecklistListener;
import com.slack.motometer.ui.adapters.ChecklistAdapter;

import java.util.List;

public class PreRide extends AppCompatActivity implements ChecklistListener {

    // UI components
    private ListView checklistItemContainer;
    private TextView readyNotReady;

    // Logic components
    private ProfileRepository profileRepository;
    private ChecklistRepository checklistRepository;
    private String profileId;
    private List<ChecklistItem> checklistItems;
    private ArrayAdapter<ChecklistItem> checklistAdapter;
    private Profile profile;

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

        // Fetch active profile
        profileRepository = new ProfileService(this);
        profile = profileRepository.getProfile(Integer.parseInt(profileId));

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
                        onResume(); // refresh adapter in delete method?
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
        checklistAdapter.clear();
        checklistAdapter.addAll(checklistItems);
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
