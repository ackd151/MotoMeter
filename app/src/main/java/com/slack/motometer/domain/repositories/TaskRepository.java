package com.slack.motometer.domain.repositories;

import com.slack.motometer.domain.model.Task;

import java.util.List;

public interface TaskRepository {

    void addTask(Task task);

    Task getTask(int id);

    List<Task> getProfileTasks(int profileId);

    int updateTask(Task task);

    void deleteTask(Task task);

}
