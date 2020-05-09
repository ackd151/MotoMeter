package com.slack.motometer.domain.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.slack.motometer.domain.db.DatabaseHelper;
import com.slack.motometer.domain.model.ChecklistItem;
import com.slack.motometer.domain.repositories.ChecklistRepository;

import java.util.ArrayList;

import static com.slack.motometer.domain.db.DatabaseHelper.CHECKLIST_TABLE_NAME;
import static com.slack.motometer.domain.db.DatabaseHelper.CL_ITEM_COMPLETE;
import static com.slack.motometer.domain.db.DatabaseHelper.CL_ITEM_TITLE;
import static com.slack.motometer.domain.db.DatabaseHelper.PROFILE_ID;
import static com.slack.motometer.domain.db.DatabaseHelper._ID;

// SQLite implementation of ChecklistRepository
public class ChecklistService implements ChecklistRepository {

    private DatabaseHelper dbHelper;

    public ChecklistService(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public long addChecklistItem(ChecklistItem checklistItem) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PROFILE_ID, checklistItem.getProfileId());
        contentValues.put(CL_ITEM_TITLE, checklistItem.getClItemTitle());
        contentValues.put(CL_ITEM_COMPLETE, checklistItem.isComplete());

        return db.insert(CHECKLIST_TABLE_NAME, null, contentValues);
    }

    @Override
    public ChecklistItem getChecklistItem(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(CHECKLIST_TABLE_NAME, new String[]{_ID, PROFILE_ID, CL_ITEM_TITLE,
                        CL_ITEM_COMPLETE}, _ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return new ChecklistItem(c.getString(0), c.getString(1), c.getString(2),
                c.getInt(3) == 1);
    }

    @Override
    public ArrayList<ChecklistItem> getProfileChecklistItems(int profileId) {
        ArrayList<ChecklistItem> tasks = new ArrayList<>();
        // Select ALL query
        String selectQuery = "SELECT * FROM " + CHECKLIST_TABLE_NAME
                + " WHERE " + PROFILE_ID + " = ?";

        // Get db
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, new String[]{String.valueOf(profileId)});
        // Loop through all rows and add tasks to list
        if (c.moveToFirst()) {
            do {
                tasks.add(new ChecklistItem(c.getString(0), c.getString(1), c.getString(2),
                        c.getInt(3) == 1));
            } while (c.moveToNext());
        }
        return tasks;
    }

    @Override
    public long updateChecklistItem(ChecklistItem checklistItem) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PROFILE_ID, checklistItem.getProfileId());
        contentValues.put(CL_ITEM_TITLE, checklistItem.getClItemTitle());
        contentValues.put(CL_ITEM_COMPLETE, checklistItem.isComplete());

        return db.update(CHECKLIST_TABLE_NAME, contentValues, _ID + "= ?",
                new String[]{ checklistItem.getId() });
    }

    @Override
    public void deleteChecklistItem(ChecklistItem checklistItem) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(CHECKLIST_TABLE_NAME, _ID + "= ?",
                new String[] { checklistItem.getId() });
    }

}
