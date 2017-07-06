package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshyk on 10.06.2017.
 */

class CancelCompletionSource<PState> implements TaskQueueService<PState> {

    private Executor executor;
    private final Object mLock = new Object();
    private OnCanceledListener<? super PState> pResultOnSuccessListener;
    private boolean keepSynced;

    CancelCompletionSource(Executor executor, OnCanceledListener<? super PState> pResultOnSuccessListener, boolean keepSynced) {
        this.executor = executor;
        this.pResultOnSuccessListener = pResultOnSuccessListener;
        this.keepSynced = keepSynced;
    }

    @Override
    public void sync(@NonNull Task<PState> pStateTask) {

        if (!(pStateTask instanceof CancellableTask))
            return;

        synchronized (mLock) {

            final CancellableTask<PState> pResultTask = (CancellableTask<PState>) pStateTask;

            if (pResultOnSuccessListener==null)
                return;

            if (executor == null)
                throw new NullPointerException("Executor must not be null!");

            if (needSync(pResultTask)) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        pResultOnSuccessListener.OnCancel(pResultTask.getProgress());
                    }
                });
            }
        }
    }

    @Override
    public boolean maybeRemove(Object criteria) {
            synchronized (mLock) {
                return criteria instanceof OnCanceledListener && criteria.equals(pResultOnSuccessListener);
            }
    }

    @Override
    public boolean needSync(@NonNull Task<PState> pStateTask) {
        if (!(pStateTask instanceof CancellableTask))
            return false;
        final CancellableTask<PState> pResultTask = (CancellableTask<PState>) pStateTask;
        return !pResultTask.isInProgress() && pResultTask.isCanceled();
    }

    @Override
    public boolean isKeepSynced() {
        synchronized (mLock) {
            return keepSynced;
        }
    }
}