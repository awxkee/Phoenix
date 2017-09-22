package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshik on 04.06.2017.
 */

class CompleteCompletionSource<PResult> implements TaskQueueService<PResult> {

    private Executor executor;
    private final Object mLock = new Object();
    private OnCompleteListener<PResult> pResultOnCompleteListener;
    private boolean keepSynced;

    CompleteCompletionSource(Executor executor, OnCompleteListener<PResult> pResultOnCompleteListener, boolean keepSynced) {
        this.executor = executor;
        this.pResultOnCompleteListener = pResultOnCompleteListener;
        this.keepSynced = keepSynced;
    }

    @Override
    public void sync(@NonNull final Task<PResult> pResultTask) {
        synchronized (mLock)
        {
            if (executor==null)
                throw new NullPointerException("Executor must not be null!");

            if (pResultOnCompleteListener==null)
                return;

            if (needSync(pResultTask))
            {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        pResultOnCompleteListener.OnComplete(pResultTask.getResult());
                    }
                });
            }
        }
    }

    @Override
    public boolean maybeRemove(Object criteria) {
        synchronized (mLock) {
            return criteria instanceof OnCompleteListener && criteria.equals(pResultOnCompleteListener);
        }
    }

    @Override
    public boolean needSync(@NonNull Task<PResult> pResultTask) {
        return pResultTask.isComplete();
    }

    @Override
    public boolean isKeepSynced() {
        synchronized (mLock) {
            return keepSynced;
        }
    }
}
