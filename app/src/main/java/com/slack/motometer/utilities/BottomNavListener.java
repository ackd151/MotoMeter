package com.slack.motometer.utilities;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.slack.motometer.R;
import com.slack.motometer.ui.activities.MainActivity;
import com.slack.motometer.ui.activities.Notes;
import com.slack.motometer.ui.activities.PostRide;
import com.slack.motometer.ui.activities.PreRide;
import com.slack.motometer.ui.activities.TasksOverview;

public class BottomNavListener implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private String profileId;

    public BottomNavListener(Context context, String profileId) {
        this.context = context;
        this.profileId = profileId;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.bottom_nav_home:
                    Intent homeIntent = new Intent(context, MainActivity.class);
                    context.startActivity(homeIntent);
                    return true;
                case R.id.bottom_nav_maintenance:
                    Intent maintenanceIntent = new Intent(context, TasksOverview.class);
                    maintenanceIntent.putExtra("profileId", profileId);
                    context.startActivity(maintenanceIntent);
                    return true;
                case R.id.bottom_nav_post_ride:
                    Intent postRideIntent = new Intent(context, PostRide.class);
                    postRideIntent.putExtra("profileId", profileId);
                    context.startActivity(postRideIntent);
                    return true;
                case R.id.bottom_nav_pre_ride:
                    Intent preRideIntent = new Intent(context, PreRide.class);
                    preRideIntent.putExtra("profileId", profileId);
                    context.startActivity(preRideIntent);
                    return true;
                case R.id.bottom_nav_notes:
                    Intent notesIntent = new Intent(context, Notes.class);
                    notesIntent.putExtra("profileId", profileId);
                    context.startActivity(notesIntent);
                    return true;
            }
            return false;
    }
}
