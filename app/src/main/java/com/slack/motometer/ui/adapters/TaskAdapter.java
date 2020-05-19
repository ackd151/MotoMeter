package com.slack.motometer.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.slack.motometer.R;
import com.slack.motometer.domain.logic.TaskLogic;
import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.model.Task;
import com.slack.motometer.domain.services.ProfileService;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {

    private Context context;
    private List<Task> tasks;

    public TaskAdapter(Context context, int resource, List<Task> tasks) {
        super(context, resource, tasks);
        this.context = context;
        this.tasks = tasks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.task_card_view, null);
        }

        Task task = tasks.get(position);
        // fetch associated profile from db to get current operating hours
        ProfileService profileService = new ProfileService(context);
        Profile profile = profileService.getProfile(Integer.parseInt(task.getProfileId()));

        // set task title textview
        TextView taskTitleTV = view.findViewById(R.id.task_card_view_title_tv);
        String taskTitle = task.getTaskTitle();
        taskTitleTV.setText(taskTitle);

        // set remaining hours textview
        TextView taskRemainingHoursTV = view.findViewById(R.id.task_card_view_hour_value_tv);
        String remainingHours = new TaskLogic(context, profile.getId()).getRemainingHours(task);
        taskRemainingHoursTV.setText(remainingHours);
        // set appropriate color:  < 2 hours -> yellow, < 0 -> red, else -> green
        float hoursLeft = Float.parseFloat(remainingHours);
        taskRemainingHoursTV.setTextColor(hoursLeft <= 0 ? ContextCompat.getColor(context,
                R.color.danger) : hoursLeft <= 2 ? ContextCompat.getColor(context,
                R.color.caution) : ContextCompat.getColor(context, R.color.accent_pressed));

        return view;
    }
}
