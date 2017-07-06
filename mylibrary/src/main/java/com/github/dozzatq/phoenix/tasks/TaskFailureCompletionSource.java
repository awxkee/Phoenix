package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshyk on 23.06.2017.
 */

class TaskFailureCompletionSource<PResult> implements TaskQueueService<PResult> {

    private Executor executor;
    private final Object mLock = new Object();
    private OnTaskFailureListener<PResult> pResultFailureListener;
    private boolean keepSynced;

    TaskFailureCompletionSource(Executor executor, OnTaskFailureListener<PResult> pResultFailureListener, boolean keepSynced) {
        this.executor = executor;
        this.pResultFailureListener = pResultFailureListener;
        this.keepSynced = keepSynced;
    }

    @Override
    public void sync(@NonNull final Task<PResult> pResultTask) {
        synchronized (mLock)
        {
            if (pResultFailureListener==null)
                return;

            if (executor==null)
                throw new NullPointerException("Executor & OnFailureListener must not be null!");

            if (needSync(pResultTask))
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
        synchronized (mLock) {
            return criteria instanceof OnFailureListener && criteria.equals(pResultFailureListener);
        }
    }

    @Override
    public boolean needSync(@NonNull Task<PResult> pResultTask) {
        return pResultTask.isExcepted();
    }

    @Override
    public boolean isKeepSynced() {
        synchronized (mLock) {
            return keepSynced;
        }
    }
}
