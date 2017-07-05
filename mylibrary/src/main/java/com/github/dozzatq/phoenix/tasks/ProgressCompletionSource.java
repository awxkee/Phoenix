package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshyk on 10.06.2017.
 */

class ProgressCompletionSource<PState> implements StateQueueService<PState> {

    private Executor executor;
    private final Object waitObject=new Object();
    private OnProgressListener<? super PState> pResultOnSuccessListener;

    ProgressCompletionSource(Executor executor, OnProgressListener<? super PState> pResultOnSuccessListener) {
        this.executor = executor;
        this.pResultOnSuccessListener = pResultOnSuccessListener;
    }

    @Override
    public void shout(@NonNull final CancellableTask<PState> pResultTask) {
        synchronized (waitObject) {
            if (executor == null || pResultOnSuccessListener == null)
                throw new NullPointerException("Executor & OnProgressListener must not be null!");

            if (pResultTask.isInProgress() && !pResultTask.isCanceled()) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        pResultOnSuccessListener.OnProgress(pResultTask.getProgress());
                    }
                });
            }
        }
    }

    @Override
    public boolean maybeRemove(Object criteria) {
        synchronized (waitObject) {
            if (criteria instanceof OnProgressListener)
                if (criteria.equals(pResultOnSuccessListener))
                    return true;
            return false;
        }
    }
}
