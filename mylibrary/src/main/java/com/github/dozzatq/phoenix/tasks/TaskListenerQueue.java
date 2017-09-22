package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * Created by Rodion Bartoshik on 04.06.2017.
 */

class TaskListenerQueue<PResult> {
    private final Object mLock = new Object();
    private ArrayDeque<TaskQueueService<PResult>> taskCompleteListeners;

    TaskListenerQueue()
    {
        taskCompleteListeners = new ArrayDeque<>();
    }

    void addService(TaskQueueService<PResult> pResultTaskQueueService, Task<PResult> resultTask)
    {
        synchronized (mLock) {
            taskCompleteListeners.add(pResultTaskQueueService);
            if (pResultTaskQueueService.needSync(resultTask))
                callForThis(pResultTaskQueueService, resultTask);
        }
    }

    void removeFromQueue(Object listenerCriteria)
    {
        if (listenerCriteria==null)
            return;
        synchronized (mLock)
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

    private void callForThis(TaskQueueService<PResult> taskCompleteListener, @NonNull Task<PResult> pResultTask)
    {
        synchronized (mLock)
        {
            if (taskCompleteListener==null)
                return;

            if (taskCompleteListener.needSync(pResultTask)) {
                taskCompleteListener.sync(pResultTask);
                if (!taskCompleteListener.isKeepSynced())
                    if (taskCompleteListeners.contains(taskCompleteListener))
                        taskCompleteListeners.remove(taskCompleteListener);
            }
        }
    }

    void callQueue(@NonNull Task<PResult> pResultTask)
    {
        synchronized (mLock)
        {
            if (taskCompleteListeners==null)
                return;

            Iterator<TaskQueueService<PResult>> iterator = taskCompleteListeners.descendingIterator();
            while (iterator.hasNext())
            {
                TaskQueueService<PResult> pResultTaskQueueService = iterator.next();
                if (pResultTaskQueueService == null)
                    throw new NullPointerException("TaskQueueService must not be null");
                if (pResultTaskQueueService.needSync(pResultTask)) {
                    pResultTaskQueueService.sync(pResultTask);
                    if (!pResultTaskQueueService.isKeepSynced())
                        iterator.remove();
                }
            }

        }
    }


}
