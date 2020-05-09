package com.slack.motometer.domain.repositories;

import com.slack.motometer.domain.model.Note;

public interface NoteRepository {

    long addNote(Note note);

    Note getNote(int id);

    Note getNoteByProfileId(String id);

    long updateNote(Note note);

    void deleteNote(Note note);
}
