package com.github.dozzatq.phoenix.Tasks;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by dxfb on 30.05.2017.
 */

public class TaskAlliance extends Task<Void> implements OnCompleteListener, OnFailureListener {

    private ArrayDeque<Task> allianceTasks = new ArrayDeque<>();

    public TaskAlliance(Task... tasks)
    {
        for (Task task : tasks) {
            addEndPointForEach(task);
            allianceTasks.add(task);
        }
    }

    public TaskAlliance(Collection<? extends Task<?>> taskCollection)
    {
        for (Task task : taskCollection) {
            addEndPointForEach(task);
            allianceTasks.add(task);
        }
    }

    public TaskAlliance(Task task)
    {
        addEndPointForEach(task);
        allianceTasks.add(task);
    }

    public TaskAlliance addTask(Task task)
    {
        addEndPointForEach(task);
        allianceTasks.add(task);
        return this;
    }

    public TaskAlliance removeTask(Task task)
    {
        if (allianceTasks.contains(task))
            allianceTasks.remove(task);
        return this;
    }

    private void addEndPointForEach(Task task)
    {
        task.addOnCompleteListener(this);
        task.addOnFailureListener(this);
    }

    @Override
    public void OnFailure(Exception exception) {
        setException(exception);
    }

    @Override
    public void OnComplete(Object o) {
        boolean hasNoComplete = false;

        Iterator<Task> taskIterator = allianceTasks.descendingIterator();
        while (taskIterator.hasNext())
        {
            Task allianceTask  = taskIterator.next();
            if (!allianceTask.isComplete())
            {
                hasNoComplete = true;
            }
        }
        if (!hasNoComplete )
            setResult(null);
    }

}
