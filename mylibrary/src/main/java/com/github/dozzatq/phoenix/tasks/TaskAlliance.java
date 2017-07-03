package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.Collection;

/**
 * Created by dxfb on 30.05.2017.
 */

public class TaskAlliance extends Task<Void> implements OnTaskSuccessListener, OnTaskFailureListener {

    int taskCount = 0;
    final Object mLock = new Object();

    public TaskAlliance(Task... tasks)
    {
        for (Task task : tasks) {
            addEndPointForEach(task);
            taskCount++;
        }
    }

    public TaskAlliance(Collection<Task> taskCollection)
    {
        for (Task task : taskCollection) {
            addEndPointForEach(task);
            taskCount++;
        }
    }

    public TaskAlliance(Task task)
    {
        addEndPointForEach(task);
        taskCount++;
    }

    private void addEndPointForEach(Task task)
    {
        task.addOnTaskSuccessListener(this);
        task.addOnTaskFailureListener(this);
    }

    ArrayDeque<Task> exceptedTask = new ArrayDeque<>();
    ArrayDeque<Task> successTask = new ArrayDeque<>();

    Exception exception;
    private volatile boolean hasException;

    boolean hasExcepted()
    {
        synchronized (mLock)
        {
            return hasException;
        }
    }

    protected void checkTasks()
    {
        synchronized (mLock)
        {
            if (exceptedTask.size() + successTask.size() == taskCount)
            {
                if (hasExcepted())
                    setException(exception);
                else
                    setResult(null);

                exceptedTask.clear();
                successTask.clear();
            }
        }
    }

    @Override
    public void OnTaskException(@NonNull Task task) {
        synchronized (mLock)
        {
            if (!exceptedTask.contains(task))
                exceptedTask.add(task);
            hasException = true;
            exception = task.getException();
            checkTasks();
        }
    }

    @Override
    public void OnTaskSuccess(@NonNull Task task) {
        synchronized (mLock)
        {
            if (!successTask.contains(task))
                successTask.add(task);
            checkTasks();
        }
    }
}
