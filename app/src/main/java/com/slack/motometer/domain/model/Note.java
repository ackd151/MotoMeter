package com.slack.motometer.domain.model;

public class Note {

    private String id, profileId, contents;

    public Note(String id, String profileId, String contents) {
        this.id = id;
        this.profileId = profileId;
        this.contents = contents;
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

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
