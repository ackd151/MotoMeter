package com.slack.motometer.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.slack.motometer.R;
import com.slack.motometer.domain.logic.ProfileLogic;
import com.slack.motometer.domain.model.Note;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.repositories.NoteRepository;
import com.slack.motometer.domain.repositories.ProfileRepository;
import com.slack.motometer.domain.services.NoteService;
import com.slack.motometer.domain.services.ProfileService;

public class Notes extends AppCompatActivity {

    // UI components
    private EditText noteContents;

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
        noteContents = findViewById(R.id.notes_content_et);

        // Set UI components
        noteContents.setText(note.getContents());
        // Start new line when starting notes activity - unless empty
        if (note.getContents().length() != 0) {
            noteContents.append("\n");
        }
        noteContents.setSelection(noteContents.getText().length());
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
            // Back button - return to previous activity
            case android.R.id.home:
                finish();
                return true;
            // Save notes to db
            case R.id.toolbar_notes_icon_save:
                noteRepository.updateNote(new Note(note.getId(), note.getProfileId(),
                        noteContents.getText().toString()));
                finish();
                return true;
            // Clear notes and record in db
            case R.id.toolbar_notes_icon_clear:
                noteContents.setText("");
                noteRepository.updateNote(new Note(note.getId(), note.getProfileId(),
                        noteContents.getText().toString()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
