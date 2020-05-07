package com.slack.motometer.domain.logic;

import android.content.Context;

import com.slack.motometer.domain.model.ChecklistItem;
import com.slack.motometer.domain.repositories.ChecklistRepository;
import com.slack.motometer.domain.services.ChecklistService;

import java.util.List;

public class ChecklistLogic {

    private ChecklistRepository checklistRepository;
    private Context context;

    public ChecklistLogic(Context context) {
        this.context = context;
        checklistRepository = new ChecklistService(context);
    }

    // Record ChecklistItem.isComplete in db
    public void updateIsComplete(ChecklistItem checklistItem, boolean isChecked) {
        checklistRepository.updateChecklistItem(new ChecklistItem(checklistItem.getId(),
                checklistItem.getProfileId(), checklistItem.getClItemTitle(), isChecked));
    }

    // Reset all pre-ride inspection items for profileId arg
    public void resetChecklist(int profileId) {
        // Fetch all checklist items from db
        List<ChecklistItem> checklistItems =
                checklistRepository.getProfileChecklistItems(profileId);
        // Set all checklistItem.isComplete false and update db records
        for (ChecklistItem checklistItem : checklistItems) {
            checklistItem.setComplete(false);
            checklistRepository.updateChecklistItem(checklistItem);
        }
    }

    // Check if all checklist items are complete
    public boolean isReady(List<ChecklistItem> checklistItems) {
        for (ChecklistItem checklistItem : checklistItems) {
            if (!checklistItem.isComplete()) {
                return false;
            }
        }
        return true;
    }
}
