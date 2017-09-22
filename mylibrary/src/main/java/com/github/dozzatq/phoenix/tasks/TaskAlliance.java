package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.AnyThread;
import android.support.annotation.GuardedBy;
import android.support.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Rodion Bartoshik on 30.05.2017.
 */

@AnyThread
public class TaskAlliance extends Task<Void> implements OnTaskSuccessListener, OnTaskFailureListener {

    int taskCount = 0;
    final Object mLock = new Object();

    public TaskAlliance(@NonNull TaskSource taskSource)
    {
        this(Tasks.execute(taskSource));
    }

    public TaskAlliance(@NonNull TaskSource... taskSources)
    {
        List<Task> taskList=new ArrayList<>();
        for (TaskSource source : taskSources) {
            taskList.add(Tasks.execute(source));
        }
        initCollection(taskList);
    }

    public TaskAlliance(@NonNull Task... tasks)
    {
        for (Task task : tasks) {
            addEndPointForEach(task);
            taskCount++;
        }
    }

    public TaskAlliance(@NonNull Collection<? extends Task> taskCollection)
    {
        initCollection(taskCollection);
    }

    private void initCollection(Collection<? extends Task> taskCollection) {
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

    @GuardedBy("mLock")
    ArrayDeque<Task> exceptedTask = new ArrayDeque<>();
    @GuardedBy("mLock")
    ArrayDeque<Task> successTask = new ArrayDeque<>();

    Exception exception;

    @GuardedBy("mLock")
    private volatile boolean hasException;

    boolean hasExcepted()
    {
        synchronized (mLock)
        {
            return hasException;
        }
    }

    public boolean isFinished()
    {
        synchronized (mLock)
        {
            return exceptedTask.size() + successTask.size() == taskCount;
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
