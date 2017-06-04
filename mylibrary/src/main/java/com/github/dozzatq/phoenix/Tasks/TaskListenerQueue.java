package com.github.dozzatq.phoenix.Tasks;

import android.support.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * Created by dxfb on 04.06.2017.
 */

class TaskListenerQueue<PResult> {
    private final Object waitObject = new Object();
    private ArrayDeque<TaskQueueService<PResult>> taskCompleteListeners;
    private volatile boolean keepSynced;

    public void addService(TaskQueueService<PResult> pResultTaskQueueService)
    {
        synchronized (waitObject) {
            if (taskCompleteListeners == null)
            {
                taskCompleteListeners = new ArrayDeque<>();
            }
            taskCompleteListeners.add(pResultTaskQueueService);
        }
    }

    public void removeFromQueue(Object listenerCriteria)
    {
        if (listenerCriteria==null)
            return;
        synchronized (waitObject)
        {
            Iterator<TaskQueueService<PResult>> iterator = taskCompleteListeners.descendingIterator();
            while (iterator.hasNext())
            {
                TaskQueueService<PResult> pResultTaskQueueService = iterator.next();
                if (pResultTaskQueueService.maybeRemove(listenerCriteria)) {
                    iterator.remove();
                    return;
                }
            }
        }
    }

    public void callForThis(TaskQueueService<PResult> taskCompleteListener, @NonNull Task<PResult> pResultTask)
    {
        synchronized (waitObject)
        {
            if (taskCompleteListener==null)
                return;

            taskCompleteListener.OnTaskComplete(pResultTask);
            if (!isSynced())
                if (taskCompleteListeners.contains(taskCompleteListener))
                    taskCompleteListeners.remove(taskCompleteListener);
        }
    }

    public void callQueue(@NonNull Task<PResult> pResultTask)
    {
        synchronized (waitObject)
        {
            if (taskCompleteListeners==null)
                return;

            Iterator<TaskQueueService<PResult>> iterator = taskCompleteListeners.descendingIterator();
            while (iterator.hasNext())
            {
                TaskQueueService<PResult> pResultTaskQueueService = iterator.next();
                if (pResultTaskQueueService ==null)
                    throw  new NullPointerException("TaskQueueService must not be null");
                pResultTaskQueueService.OnTaskComplete(pResultTask);
                if (!isSynced())
                    iterator.remove();
            }

        }
    }

    public boolean isSynced() {
        synchronized (waitObject) {
            return keepSynced;
        }
    }

    public void keepSynced(boolean keepSynced) {
        synchronized (waitObject) {
            this.keepSynced = keepSynced;
        }
    }
}
