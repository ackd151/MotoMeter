package com.slack.motometer.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.slack.motometer.R;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.repositories.ProfileRepository;
import com.slack.motometer.domain.services.ProfileService;
import com.slack.motometer.ui.adapters.ProfilePagerAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // UI components
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView emptyView;

    // Logic Components
    private FragmentStatePagerAdapter profilePagerAdapter;
    private ProfileRepository profileRepository;
    private List<Profile> profiles;

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

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // Get profiles
        profileRepository = new ProfileService(this);
        profiles = profileRepository.getAllProfiles();

        // Set empty view visibility
        emptyView = findViewById(R.id.empty_list_item);
        emptyView.setVisibility(profiles.size() == 0 ? View.VISIBLE : View.GONE);

        viewPager = findViewById(R.id.main_view_pager);
        profilePagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager(), profiles, this);
        viewPager.setAdapter(profilePagerAdapter);

        // Set tab_layout tabMode
        tabLayout.setTabMode(profiles.size() <= 4 ? TabLayout.MODE_FIXED : TabLayout.MODE_SCROLLABLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // refresh profiles object to display up-to-date listview
        profiles = profileRepository.getAllProfiles();
        // clear adapter and repopulate with fresh profiles data
        profilePagerAdapter.notifyDataSetChanged(); // notify so FragmentManager refreshes
        profilePagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager(), profiles, this);
        viewPager.setAdapter(profilePagerAdapter);
        // Set tab_layout tabMode
        tabLayout.setTabMode(profiles.size() <= 4 ? TabLayout.MODE_FIXED : TabLayout.MODE_SCROLLABLE);

        // Set empty view visibility
        emptyView = findViewById(R.id.empty_list_item);
        emptyView.setVisibility(profiles.size() == 0 ? View.VISIBLE : View.GONE);
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
