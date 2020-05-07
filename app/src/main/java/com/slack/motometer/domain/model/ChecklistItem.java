package com.slack.motometer.domain.model;

// ChecklistIem is a user created checklist task
public class ChecklistItem {

    private String id; // primary key in db
    private String profileId; // foreign key in db references Profile.id
    private String clItemTitle; // The name of the checklist task
    private boolean isComplete; // checklist task currently completed?

    public ChecklistItem(String profileId, String clItemTitle) {
        this.profileId = profileId;
        this.clItemTitle = clItemTitle;
        isComplete = false;
    }

    public ChecklistItem(String id, String profileId, String clItemTitle, boolean isComplete) {
        this.id = id;
        this.profileId = profileId;
        this.clItemTitle = clItemTitle;
        this.isComplete = isComplete;
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

    public String getClItemTitle() {
        return clItemTitle;
    }

    public void setClItemTitle(String clItemTitle) {
        this.clItemTitle = clItemTitle;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}
