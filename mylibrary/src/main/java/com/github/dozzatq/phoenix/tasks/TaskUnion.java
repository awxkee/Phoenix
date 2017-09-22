package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;

import java.util.Collection;

/**
 * Created by Rodion Bartoshik on 23.06.2017.
 */

@AnyThread
public class TaskUnion extends TaskAlliance{

    public TaskUnion(@NonNull TaskSource taskSource) {
        super(taskSource);
    }

    public TaskUnion(@NonNull TaskSource... taskSources) {
        super(taskSources);
    }

    public TaskUnion(@NonNull Task... tasks) {
        super(tasks);
    }

    public TaskUnion(@NonNull Collection<? extends Task> taskCollection) {
        super(taskCollection);
    }

    public TaskUnion(@NonNull Task task) {
        super(task);
    }

    @Override
    protected void checkTasks()
    {
        synchronized (mLock)
        {
            if (exceptedTask.size() + successTask.size() == taskCount)
            {
                if (hasExcepted())
                    setException(exception);

                if (successTask.size()>0)
                    setResult(null);

                exceptedTask.clear();
                successTask.clear();
            }
        }
    }
}