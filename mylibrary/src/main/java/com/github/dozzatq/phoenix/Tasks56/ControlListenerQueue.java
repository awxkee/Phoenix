package com.github.dozzatq.phoenix.Tasks56;

import android.support.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * Created by dxfb on 10.06.2017.
 */

public class ControlListenerQueue<PState> {
    private final Object waitObject = new Object();
    private ArrayDeque<ControlQueueService<PState>> taskPauseListenersQueue;
    private volatile boolean keepSynced;

    public ControlListenerQueue() {
        taskPauseListenersQueue = new ArrayDeque<>();
    }

    public void callQueue(@NonNull ControllableTask<PState> pResultTask)
    {
        synchronized (waitObject)
        {
            if (taskPauseListenersQueue ==null)
                return;

            Iterator<ControlQueueService<PState>> iterator = taskPauseListenersQueue.descendingIterator();
            while (iterator.hasNext())
            {
                ControlQueueService<PState> pResultTaskQueueService = iterator.next();
                if (pResultTaskQueueService ==null)
                    throw  new NullPointerException("TaskQueueService must not be null");
                pResultTaskQueueService.shout(pResultTask);
                if (!isSynced())
                    iterator.remove();
            }
        }
    }

    public void callForThis(ControlQueueService<PState> taskCompleteListener,
                            @NonNull ControllableTask< PState> pResultTask)
    {
        synchronized (waitObject)
        {
            if (taskCompleteListener==null)
                return;

            taskCompleteListener.shout(pResultTask);
            if (!isSynced())
                if (taskPauseListenersQueue.contains(taskCompleteListener))
                    taskPauseListenersQueue.remove(taskCompleteListener);
        }
    }

    public void push(ControlQueueService<PState> ControlQueueService)
    {
        synchronized (waitObject)
        {
            taskPauseListenersQueue.add(ControlQueueService);
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
            Iterator<ControlQueueService<PState>> iterator = taskPauseListenersQueue.descendingIterator();
            while (iterator.hasNext())
            {
                ControlQueueService<PState> pResultTaskQueueService = iterator.next();
                if (pResultTaskQueueService.maybeRemove(listenerCriteria)) {
                    iterator.remove();
                    return;
                }
            }
        }
    }
}
