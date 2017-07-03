package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by dxfb on 10.06.2017.
 */

class CancelCompletionSource<PState> implements StateQueueService<PState> {

    private Executor executor;
    private final Object waitObject = new Object();
    private OnCanceledListener<? super PState> pResultOnSuccessListener;

    CancelCompletionSource(Executor executor, OnCanceledListener<? super PState> pResultOnSuccessListener) {
        this.executor = executor;
        this.pResultOnSuccessListener = pResultOnSuccessListener;
    }

    @Override
    public void shout(@NonNull final CancellableTask<PState> pResultTask) {
        synchronized (waitObject) {
            if (executor == null || pResultOnSuccessListener == null)
                throw new NullPointerException("Executor & OnSuccessListener must not be null!");

            if (!pResultTask.isInProgress() && pResultTask.isCanceled()) {
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
            synchronized (waitObject) {
            if (criteria instanceof OnCanceledListener)
                if (criteria.equals(pResultOnSuccessListener))
                    return true;
            return false;
        }
    }
}