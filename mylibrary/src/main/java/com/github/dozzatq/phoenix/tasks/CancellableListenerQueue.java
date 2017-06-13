package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * Created by dxfb on 10.06.2017.
 */

class CancellableListenerQueue<PState> {
    private final Object waitObject = new Object();
    private ArrayDeque<StateQueueService<PState>> taskProgressListener;
    private volatile boolean keepSynced;

    public CancellableListenerQueue() {
        taskProgressListener = new ArrayDeque<>();
    }

    public void callQueue(@NonNull CancellableTask<PState> pResultTask)
    {
        synchronized (waitObject)
        {
            if (taskProgressListener==null)
                return;

            Iterator<StateQueueService<PState>> iterator = taskProgressListener.descendingIterator();
            while (iterator.hasNext())
            {
                StateQueueService<PState> pResultTaskQueueService = iterator.next();
                if (pResultTaskQueueService ==null)
                    throw  new NullPointerException("TaskQueueService must not be null");
                pResultTaskQueueService.shout(pResultTask);
                if (!isSynced())
                    iterator.remove();
            }
        }
    }

    public void callForThis(StateQueueService<PState> taskCompleteListener,
                            @NonNull CancellableTask< PState> pResultTask)
    {
        synchronized (waitObject)
        {
            if (taskCompleteListener==null)
                return;

            taskCompleteListener.shout(pResultTask);
            if (!isSynced())
                if (taskProgressListener.contains(taskCompleteListener))
                    taskProgressListener.remove(taskCompleteListener);
        }
    }

    public void push(StateQueueService<PState> stateQueueService)
    {
        synchronized (waitObject)
        {
            taskProgressListener.add(stateQueueService);
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

    public void removeFromQueue(Object listenerCriteria)
    {
        if (listenerCriteria==null)
            return;
        synchronized (waitObject)
        {
            Iterator<StateQueueService<PState>> iterator = taskProgressListener.descendingIterator();
            while (iterator.hasNext())
            {
                StateQueueService<PState> pResultTaskQueueService = iterator.next();
                if (pResultTaskQueueService.maybeRemove(listenerCriteria)) {
                    iterator.remove();
                    return;
                }
            }
        }
    }

}
