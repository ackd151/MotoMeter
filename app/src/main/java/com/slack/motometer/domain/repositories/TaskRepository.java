package com.slack.motometer.domain.repositories;

import com.slack.motometer.domain.model.Task;

import java.util.List;

public interface TaskRepository {

    long addTask(Task task);

    Task getTask(int id);

    List<Task> getProfileTasks(int profileId);

    long updateTask(Task task);

    void deleteTask(Task task);

}
