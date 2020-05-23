package com.slack.motometer.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import com.slack.motometer.utilities.BottomNavListener;

public class Notes extends AppCompatActivity {

    // UI components
    private EditText noteContentsET;
    private TextView infoPanelTV;
    private ConstraintLayout infoPanelCL;
    private BottomNavigationView navBar;

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
        // Set infoPanel
        infoPanelTV.setText(R.string.activity_notes_information);
        // Hide info panel again if user clicks help panel
        infoPanelCL.setClickable(true);
        infoPanelCL.setOnClickListener(v -> infoPanelCL.setVisibility(View.GONE));

        // Set bottom navigation bar
        navBar = findViewById(R.id.notes_nav_bar);
        navBar.setSelectedItemId(R.id.bottom_nav_notes);
        navBar.setOnNavigationItemSelectedListener(new BottomNavListener(this, profileId));
    }

    @Override
    protected void onResume() {
        super.onResume();
        navBar.getMenu().findItem(R.id.bottom_nav_notes).setChecked(true);
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
