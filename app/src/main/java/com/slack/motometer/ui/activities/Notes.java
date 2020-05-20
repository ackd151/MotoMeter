package com.slack.motometer.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.slack.motometer.R;
import com.slack.motometer.domain.model.Note;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.repositories.NoteRepository;
import com.slack.motometer.domain.repositories.ProfileRepository;
import com.slack.motometer.domain.services.NoteService;
import com.slack.motometer.domain.services.ProfileService;

public class Notes extends AppCompatActivity {

    // UI components
    private EditText noteContentsET;
    private TextView infoPanelTV;
    private ConstraintLayout infoPanelCL;

    // Logic components
    private ProfileRepository profileRepository;
    private NoteRepository noteRepository;
    private Note note;
    private String profileId;
    Profile profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        // Set toolbar
        Toolbar myToolbar = findViewById(R.id.notes_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.toolbar_notes_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set Logic components
        profileId = getIntent().getExtras().getString("profileId");
        profileRepository = new ProfileService(this);
        profile = profileRepository.getProfile(Integer.parseInt(profileId));
        noteRepository = new NoteService(this);
        note = noteRepository.getNoteByProfileId(profileId);

        // Get handle on UI components
        noteContentsET = findViewById(R.id.notes_content_et);
        infoPanelTV = findViewById(R.id.info_panel_info_text_tv);
        infoPanelCL = findViewById(R.id.info_panel_cl);

        // Set UI components
        noteContentsET.setText(note.getContents());
        // Start new line when starting notes activity - unless empty
        if (note.getContents().length() != 0) {
            noteContentsET.append("\n");
        }
        noteContentsET.setSelection(noteContentsET.getText().length());
        // Set infoPanel
        infoPanelTV.setText(R.string.activity_notes_information);
        // Hide info panel again if user clicks help panel
        infoPanelCL.setClickable(true);
        infoPanelCL.setOnClickListener(v -> infoPanelCL.setVisibility(View.GONE));

        // Set bottom navigation bar
        BottomNavigationView navBar = findViewById(R.id.notes_nav_bar);
        navBar.setSelectedItemId(R.id.bottom_nav_notes);
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

    // Inflate toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Set toolbar icon actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Back button - return to previous activity
                finish();
                return true;
            case R.id.toolbar_notes_help:
                // Show info panel
                infoPanelCL.setVisibility(View.VISIBLE);
                return true;
            case R.id.toolbar_notes_icon_save:
                // Save notes to db
                noteRepository.updateNote(new Note(note.getId(), note.getProfileId(),
                        noteContentsET.getText().toString()));
                // Show and dismiss notify notes saved alert dialog
                AlertDialog notifySavedDialog = createNotifySavedDialog();
                notifySavedDialog.show();
                Runnable dismissNotifySavedDialog = notifySavedDialog::dismiss;
                new Handler().postDelayed( dismissNotifySavedDialog, 1000 );
                return true;
            case R.id.toolbar_notes_icon_clear:
                // Clear notes EditText
                noteContentsET.setText("");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private AlertDialog createNotifySavedDialog() {
        return new AlertDialog.Builder(this)
                .setTitle("Notes Saved")
                .setMessage("")
                .setIcon(R.drawable.ic_check_mark)
                .create();
    }
}
