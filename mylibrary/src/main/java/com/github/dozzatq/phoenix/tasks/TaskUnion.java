package com.github.dozzatq.phoenix.tasks;

import java.util.Collection;

/**
 * Created by dxfb on 23.06.2017.
 */

public class TaskUnion extends TaskAlliance{

    public TaskUnion(Task... tasks) {
        super(tasks);
    }

    public TaskUnion(Collection<? extends Task<?>> taskCollection) {
        super(taskCollection);
    }

    public TaskUnion(Task task) {
        super(task);
    }

    @Override
    protected void checkTasks()
    {
        synchronized (waitObject)
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