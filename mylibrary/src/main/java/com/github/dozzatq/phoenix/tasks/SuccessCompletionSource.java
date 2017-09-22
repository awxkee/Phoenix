package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshik on 04.06.2017.
 */

class SuccessCompletionSource<PResult> implements TaskQueueService<PResult> {

    private Executor executor;
    private final Object mLock = new Object();
    private OnSuccessListener<PResult> pResultOnSuccessListener;
    private boolean keepSynced;

    SuccessCompletionSource(Executor executor, OnSuccessListener<PResult> pResultOnSuccessListener, boolean keepSynced) {
        this.executor = executor;
        this.pResultOnSuccessListener = pResultOnSuccessListener;
        this.keepSynced = keepSynced;
    }

    @Override
    public void sync(@NonNull final Task<PResult> pResultTask) {
        synchronized (mLock)
        {
            if (executor==null)
                throw new NullPointerException("Executor must not be null!");

            if (pResultOnSuccessListener == null)
                return;

            if (needSync(pResultTask))
            {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        pResultOnSuccessListener.OnSuccess((PResult) pResultTask.getResult());
                    }
                });
            }
        }
    }

    @Override
    public boolean maybeRemove(Object criteria) {
        synchronized (mLock) {
            return criteria instanceof OnSuccessListener && criteria.equals(pResultOnSuccessListener);
        }
    }

    @Override
    public boolean needSync(@NonNull Task<PResult> pResultTask) {
        return pResultTask.isSuccessful();
    }

    @Override
    public boolean isKeepSynced() {
        synchronized (mLock) {
            return keepSynced;
        }
    }
}
