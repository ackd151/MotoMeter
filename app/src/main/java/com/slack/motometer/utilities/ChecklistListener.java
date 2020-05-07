package com.slack.motometer.utilities;

// This interface is used to callback from non-context class (ChecklistAdapter) to pre ride activity
// in order to update view(s) external to the listview using the adapter
public interface ChecklistListener {

    void isChecklistComplete();
}
