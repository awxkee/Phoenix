package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by dxfb on 04.06.2017.
 */

class FailureCompletionSource<PResult> implements TaskQueueService<PResult> {

    private Executor executor;
    private final Object waitObject=new Object();
    private OnFailureListener pResultFailureListener;

    public FailureCompletionSource(Executor executor, OnFailureListener pResultFailureListener) {
        this.executor = executor;
        this.pResultFailureListener = pResultFailureListener;
    }

    @Override
    public void done(@NonNull final Task<PResult> pResultTask) {
        synchronized (waitObject)
        {
            if (executor==null || pResultFailureListener==null)
                throw new NullPointerException("Executor & OnFailureListener must not be null!");

            if (pResultTask.isExcepted())
            {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        pResultFailureListener.OnFailure(pResultTask.getException());
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
