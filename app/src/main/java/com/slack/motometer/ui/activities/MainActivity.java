package com.slack.motometer.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.slack.motometer.R;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.repositories.ProfileRepository;
import com.slack.motometer.domain.services.ProfileService;
import com.slack.motometer.ui.adapters.ProfileAdapter;
import com.slack.motometer.ui.adapters.ProfilePagerAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // UI components
    private ViewPager viewPager;

    // Logic Components
    private FragmentPagerAdapter profilePagerAdapter;
    private ProfileRepository profileRepository;
    private List<Profile> profiles;
//    private ArrayAdapter<Profile> profileListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.toolbar_main_title);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // Get profiles
        profileRepository = new ProfileService(this);
        profiles = profileRepository.getAllProfiles();

        // Set empty view visibility
        TextView emptyView = findViewById(R.id.empty_list_item);
        emptyView.setVisibility(profiles.size() == 0 ? View.VISIBLE : View.GONE);

        viewPager = findViewById(R.id.main_view_pager);
        profilePagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager(), profiles);
        viewPager.setAdapter(profilePagerAdapter);
    }




//        // Set Logic components
//        profileRepository = new ProfileService(this);
//        profiles = profileRepository.getAllProfiles();
//        profileListAdapter = new ProfileAdapter(this, R.layout.profile_card_view, profiles);
//
//        // Set UI components
//        profileContainer = findViewById(android.R.id.list);
//        profileContainer.setEmptyView(findViewById(R.id.empty_list_item));
//        profileContainer.setAdapter(profileListAdapter);
//        profileContainer.setClickable(true);
//        profileContainer.setOnItemClickListener((adapterView, view, i, l) -> {
//            Profile p = (Profile)profileContainer.getItemAtPosition(i);
//            Intent profileOverview = new Intent(getBaseContext(), ProfileOverview.class);
//            profileOverview.putExtra("profileId", p.getId());
//            startActivity(profileOverview);
//        });
//    }

    @Override
    protected void onResume() {
        super.onResume();
        // refresh profiles object to display up-to-date listview
        profiles = profileRepository.getAllProfiles();
        // clear adapter and repopulate with fresh profiles data
        profilePagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager(), profiles);
        viewPager.setAdapter(profilePagerAdapter);
    }

    // Inflate toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Set toolbar menu icon actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Start new profile activity
            case R.id.toolbar_main_add_profile:
                Intent intent = new Intent(this, NewProfile.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
