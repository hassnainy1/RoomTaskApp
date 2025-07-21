package com.example.roomtaskapp.controller;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.roomtaskapp.model.Task;
import com.example.roomtaskapp.model.TaskDao;
import com.example.roomtaskapp.model.TaskDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepository {

    private TaskDao taskDao;
    private LiveData<List<Task>> allTasks;

    private final ExecutorService executorService;

    public TaskRepository(Application application) {
        TaskDatabase database = TaskDatabase.getInstance(application);
        taskDao = database.taskDao();
        allTasks = taskDao.getAllTasks();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Task task) {
        executorService.execute(() -> taskDao.insert(task));
    }

    public void update(Task task) {
        executorService.execute(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        executorService.execute(() -> taskDao.delete(task));
    }

    public void deleteAllTasks() {
        executorService.execute(() -> taskDao.deleteAll());
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }
}
