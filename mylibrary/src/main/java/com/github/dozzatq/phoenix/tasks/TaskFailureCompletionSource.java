package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by dxfb on 23.06.2017.
 */

public class TaskFailureCompletionSource<PResult> implements TaskQueueService<PResult> {

    private Executor executor;
    private final Object waitObject=new Object();
    private OnTaskFailureListener<PResult> pResultFailureListener;

    public TaskFailureCompletionSource(Executor executor, OnTaskFailureListener<PResult> pResultFailureListener) {
        this.executor = executor;
        this.pResultFailureListener = pResultFailureListener;
    }

    @Override
    public void done(@NonNull final Task<PResult> pResultTask) {
        synchronized (waitObject)
        {
            if (pResultFailureListener==null)
                return;

            if (executor==null)
                throw new NullPointerException("Executor & OnFailureListener must not be null!");

            if (pResultTask.isExcepted())
            {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        pResultFailureListener.OnTaskException(pResultTask);
                    }
                });
            }
        }
    }

    @Override
    public boolean maybeRemove(Object criteria) {
        synchronized (waitObject) {
            if (criteria instanceof OnFailureListener)
                if (criteria.equals(pResultFailureListener))
                    return true;
            return false;
        }
    }
}
