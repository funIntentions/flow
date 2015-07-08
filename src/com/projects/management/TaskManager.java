package com.projects.management;

import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dan on 6/2/2015.
 */
public class TaskManager
{
    private ExecutorService executorService;
    private ArrayList<Task> runningTasks;
    private static final int MAX_NUM_THREADS = 5;
    private static int availableId = 0;

    public TaskManager()
    {
        executorService = Executors.newFixedThreadPool(MAX_NUM_THREADS);
    }

    public void submitWorker(SwingWorker worker)
    {
        executorService.execute(worker);
    }

    private Task createTask(String name, String desc)
    {
        Task task = new Task(name, desc);
        ++availableId;
        return task;
    }

    public void kill()
    {
        executorService.shutdown();
        try
        {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        }
        catch (InterruptedException exception)
        {
            // TODO: do something here.
        }
    }
}
