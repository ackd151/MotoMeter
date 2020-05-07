package com.slack.motometer.domain.model;

// Task is a maintenance requirement created by the user for a specific Profile
public class Task {

    private String id; // primary key in db
    private String profileId; // foreign key references Profile.id
    private String taskTitle; // name of maintenance task
    private float interval; // time before task is due
    private float lastCompletedAt; // last time (Profile.hours) task was completed

    public Task(String profileId, String taskTitle, String interval, String lastCompletedAt) {
        this.profileId = profileId;
        this.taskTitle = taskTitle;
        this.interval = Float.parseFloat(interval);
        this.lastCompletedAt = Float.parseFloat(lastCompletedAt);
    }

    public Task(String id, String profileId, String taskTitle, String interval, String lastCompletedAt) {
        this.id = id;
        this.profileId = profileId;
        this.taskTitle = taskTitle;
        this.interval = Float.parseFloat(interval);
        this.lastCompletedAt = Float.parseFloat(lastCompletedAt);
    }

    public Task(String id, String profileId, String taskTitle, float interval, float lastCompletedAt) {
        this.id = id;
        this.profileId = profileId;
        this.taskTitle = taskTitle;
        this.interval = interval;
        this.lastCompletedAt = lastCompletedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public float getInterval() {
        return interval;
    }

    public void setInterval(float interval) {
        this.interval = interval;
    }

    public float getLastCompletedAt() {
        return lastCompletedAt;
    }

    public void setLastCompletedAt(float lastCompletedAt) {
        this.lastCompletedAt = lastCompletedAt;
    }
}
