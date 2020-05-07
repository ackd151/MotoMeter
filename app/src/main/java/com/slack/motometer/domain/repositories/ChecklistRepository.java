package com.slack.motometer.domain.repositories;

import com.slack.motometer.domain.model.ChecklistItem;

import java.util.List;

public interface ChecklistRepository {

    void addChecklistItem(ChecklistItem checklistItem);

    ChecklistItem getChecklistItem(int id);

    List<ChecklistItem> getProfileChecklistItems(int profileId);

    int updateChecklistItem(ChecklistItem checklistItem);

    void deleteChecklistItem(ChecklistItem checklistItem);

}
