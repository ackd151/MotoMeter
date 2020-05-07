package com.slack.motometer.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.slack.motometer.R;
import com.slack.motometer.domain.logic.ChecklistLogic;
import com.slack.motometer.domain.model.ChecklistItem;
import com.slack.motometer.utilities.ChecklistListener;

import java.util.List;

public class ChecklistAdapter extends ArrayAdapter<ChecklistItem> {

    private ChecklistListener checklistListener;
    private List<ChecklistItem> checklistItems;
    private Context context;

    public ChecklistAdapter(Context context, int resource, List<ChecklistItem> checklistItems,
                            ChecklistListener checklistListener) {
        super(context, resource, checklistItems);
        this.context = context;
        this.checklistItems = checklistItems;
        this.checklistListener = checklistListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.checklist_card_view, null);
        }
        ChecklistItem checklistItem = checklistItems.get(position);
        if (checklistItem != null) {
            TextView checklistItemTitle = view.findViewById(R.id.checklist_card_title_tv);
            CheckBox checklistItemComplete = view.findViewById(R.id.checklist_card_complete_cb);
            checklistItemTitle.setText(checklistItem.getClItemTitle());
            checklistItemComplete.setChecked(checklistItem.isComplete());
            // Set onClick listener for checkbox
            checklistItemComplete.setOnClickListener(arg0 -> {
                final boolean isChecked = checklistItemComplete.isChecked();
                new ChecklistLogic(context).updateIsComplete(checklistItem, isChecked);
                checklistListener.isChecklistComplete();
            });
        }
        return view;
    }
}