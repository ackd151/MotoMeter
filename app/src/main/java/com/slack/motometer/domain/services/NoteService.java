package com.slack.motometer.domain.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.slack.motometer.domain.db.DatabaseHelper;
import com.slack.motometer.domain.model.Note;
import com.slack.motometer.domain.repositories.NoteRepository;

import static com.slack.motometer.domain.db.DatabaseHelper.NOTES_CONTENT;
import static com.slack.motometer.domain.db.DatabaseHelper.NOTES_TABLE_NAME;
import static com.slack.motometer.domain.db.DatabaseHelper.PROFILE_ID;
import static com.slack.motometer.domain.db.DatabaseHelper._ID;

// SQLite implementation of NoteRepository
public class NoteService implements NoteRepository {

    private DatabaseHelper dbHelper;

    public NoteService(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public long addNote(Note note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(PROFILE_ID, note.getProfileId());
        contentValues.put(NOTES_CONTENT, note.getContents());

        return db.insert(NOTES_TABLE_NAME, null, contentValues);
    }

    @Override
    public Note getNote(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(NOTES_TABLE_NAME, new String[]{_ID, PROFILE_ID, NOTES_CONTENT},
                _ID + "= ?", new String[]{ String.valueOf(id) },
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return new Note(cursor.getString(0), cursor.getString(1), cursor.getString(2));
    }

    @Override
    public Note getNoteByProfileId(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(NOTES_TABLE_NAME, new String[]{_ID, PROFILE_ID, NOTES_CONTENT},
                PROFILE_ID + "= ?", new String[]{ String.valueOf(id) },
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return new Note(cursor.getString(0), cursor.getString(1), cursor.getString(2));
    }

    @Override
    public long updateNote(Note note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTES_CONTENT, note.getContents());

        return db.update(NOTES_TABLE_NAME, contentValues, _ID + "= ?",
                new String[]{ note.getId() });
    }

    // Just clearing the notes, not deleting the record as it should always exist
    // change if/when better approach learned
    @Override
    public void deleteNote(Note note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String clearedNotes = "";
        updateNote(new Note(note.getId(), note.getProfileId(), clearedNotes));
    }
}
