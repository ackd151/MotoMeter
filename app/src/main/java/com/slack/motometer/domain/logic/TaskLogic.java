package com.slack.motometer.domain.logic;

import android.content.Context;

import com.slack.motometer.domain.model.Profile;
import com.slack.motometer.domain.model.Task;
import com.slack.motometer.domain.repositories.ProfileRepository;
import com.slack.motometer.domain.repositories.TaskRepository;
import com.slack.motometer.domain.services.ProfileService;
import com.slack.motometer.domain.services.TaskService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TaskLogic implements Comparator<Task> {

    private TaskRepository taskRepository;
    private ProfileRepository profileRepository;
    private Profile profile;
    private Context context;

    public enum MaintenanceDue {
        NOT, SOON, PAST
    }

    public TaskLogic(Context context, String profileId) {
        this.context = context;
        taskRepository = new TaskService(context);
        profileRepository = new ProfileService(context);
        profile = profileRepository.getProfile(Integer.parseInt(profileId));
    }

    // Calculate and return remaining hours before task is due
    public String getRemainingHours(Task task) {
        float timeRemaining = task.getInterval() -
                (Float.parseFloat(profile.getHours()) - task.getLastCompletedAt());
        timeRemaining = Math.round(timeRemaining * 10) / 10f;
        return String.valueOf(timeRemaining);
    }

    // Sign off maintenance task - sets new baseline for task interval
    public void signOffTask(Task task, Float hours) {
        task.setLastCompletedAt(hours);
        taskRepository.updateTask(task);
    }

    // Check if maintenance is due
    public MaintenanceDue isMaintenanceDue() {
        Task dueSoonest = getNextDue(Integer.parseInt(profile.getId()));
        if (dueSoonest == null) {
            return MaintenanceDue.NOT;
        }
        float dueIn = Float.parseFloat(getRemainingHours(dueSoonest));
        return dueIn <= 0 ? MaintenanceDue.PAST :
                dueIn < 2 ? MaintenanceDue.SOON :
                        MaintenanceDue.NOT;
    }

    // Get the task that is due soonest
    public Task getNextDue(int profileId) {
        List<Task> tasks = taskRepository.getProfileTasks(profileId);
        if (tasks.size() != 0) {
            Collections.sort(tasks, this);
            return tasks.get(0);
        }
        return null;
    }

    @Override
    public int compare(Task task1, Task task2) {
        Float t1 = Float.parseFloat(getRemainingHours(task1));
        Float t2 = Float.parseFloat(getRemainingHours(task2));
        return t1.compareTo(t2);
    }
}
