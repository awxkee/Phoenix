package com.github.dozzatq.phoenix.Tasks;

import android.support.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * Created by dxfb on 04.06.2017.
 */

public class TaskListenerQueue<PResult> {
    private final Object waitObject = new Object();
    private ArrayDeque<OnTaskCompleteListener<PResult>> taskCompleteListeners;
    private volatile boolean keepSynced;

    public void addService(OnTaskCompleteListener<PResult> pResultOnTaskCompleteListener)
    {
        synchronized (waitObject) {
            if (taskCompleteListeners == null)
            {
                taskCompleteListeners = new ArrayDeque<>();
            }
            taskCompleteListeners.add(pResultOnTaskCompleteListener);
        }
    }

    public void callForThis(OnTaskCompleteListener<PResult> taskCompleteListener, @NonNull Task<PResult> pResultTask)
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

    public void callComplete(@NonNull Task<PResult> pResultTask)
    {
        synchronized (waitObject)
        {
            if (taskCompleteListeners==null)
                return;

            Iterator<OnTaskCompleteListener<PResult>> iterator = taskCompleteListeners.descendingIterator();
            while (iterator.hasNext())
            {
                OnTaskCompleteListener<PResult> pResultOnTaskCompleteListener = iterator.next();
                if (pResultOnTaskCompleteListener==null)
                    throw  new NullPointerException("OnTaskCompleteListener must not be null");
                pResultOnTaskCompleteListener.OnTaskComplete(pResultTask);
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
