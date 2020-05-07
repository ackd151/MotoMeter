package com.slack.motometer.domain.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.slack.motometer.domain.db.DatabaseHelper;
import com.slack.motometer.domain.logic.TaskLogic;
import com.slack.motometer.domain.model.Task;
import com.slack.motometer.domain.repositories.TaskRepository;

import java.util.ArrayList;
import java.util.Collections;

import static com.slack.motometer.domain.db.DatabaseHelper.INTERVAL;
import static com.slack.motometer.domain.db.DatabaseHelper.LAST_COMPLETED_AT;
import static com.slack.motometer.domain.db.DatabaseHelper.PROFILE_ID;
import static com.slack.motometer.domain.db.DatabaseHelper.TASK_TABLE_NAME;
import static com.slack.motometer.domain.db.DatabaseHelper.TASK_TITLE;
import static com.slack.motometer.domain.db.DatabaseHelper._ID;

// SQLite implementation of TaskRepository
public class TaskService implements TaskRepository {

    private DatabaseHelper dbHelper;
    private Context context;

    public TaskService(Context context) {
        dbHelper = new DatabaseHelper(context);
        this.context = context;
    }

    @Override
    public void addTask(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PROFILE_ID, task.getProfileId());
        contentValues.put(TASK_TITLE, task.getTaskTitle());
        contentValues.put(INTERVAL, task.getInterval());
        contentValues.put(LAST_COMPLETED_AT, task.getLastCompletedAt());

        db.insert(TASK_TABLE_NAME, null, contentValues);
        db.close();
    }

    @Override
    public Task getTask(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(TASK_TABLE_NAME, new String[]{_ID, PROFILE_ID, TASK_TITLE, INTERVAL,
                        LAST_COMPLETED_AT}, _ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return new Task(c.getString(0), c.getString(1), c.getString(2), c.getString(3),
                c.getString(4));
    }

    @Override
    public ArrayList<Task> getProfileTasks(int profileId) {
        ArrayList<Task> tasks = new ArrayList<>();
        // Select ALL query
        String selectQuery = "SELECT * FROM " + TASK_TABLE_NAME + " WHERE " + PROFILE_ID + " = ?";

        // Get db
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, new String[]{String.valueOf(profileId)});
        // Loop through all rows and add tasks to list
        if (c.moveToFirst()) {
            do {
                tasks.add(new Task(c.getString(0), c.getString(1), c.getString(2),
                        c.getString(3), c.getString(4)));
            } while (c.moveToNext());
        }
        Collections.sort(tasks, new TaskLogic(context, String.valueOf(profileId)));
        return tasks;
    }

    @Override
    public int updateTask(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PROFILE_ID, task.getProfileId());
        contentValues.put(TASK_TITLE, task.getTaskTitle());
        contentValues.put(INTERVAL, task.getInterval());
        contentValues.put(LAST_COMPLETED_AT, task.getLastCompletedAt());

        return db.update(TASK_TABLE_NAME, contentValues, _ID + "= ?",
                new String[]{ task.getId() });
    }

    @Override
    public void deleteTask(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TASK_TABLE_NAME, _ID + "= ?", new String[] { task.getId() });
    }

}
