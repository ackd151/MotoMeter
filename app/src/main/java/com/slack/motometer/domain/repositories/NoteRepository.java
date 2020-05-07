package com.slack.motometer.domain.repositories;

import com.slack.motometer.domain.model.Note;

public interface NoteRepository {

    void addNote(Note note);

    Note getNote(int id);

    Note getNoteByProfileId(String id);

    int updateNote(Note note);

    void deleteNote(Note note);
}
