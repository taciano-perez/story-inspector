package com.o3.storyinspector.gui.status;

import javafx.concurrent.Task;
import org.controlsfx.control.TaskProgressView;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    static TaskManager singleton;

    List<Task> tasks = new ArrayList<>();

    TaskProgressView taskProgressView = new TaskProgressView();

    public static TaskManager getInstance() {
        if (singleton == null) singleton = new TaskManager();
        return singleton;
    }

    public void addTask(final Task task) {
        this.tasks.add(task);
        this.taskProgressView.getTasks().add(task);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public TaskProgressView getTaskProgressView() {
        return taskProgressView;
    }
}
