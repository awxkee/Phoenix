package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshyk on 10.06.2017.
 */

class PauseCompletionSource<PState> implements ControlQueueService<PState> {

    private Executor executor;
    private final Object waitObject=new Object();
    private OnPausedListener<? super PState> pResultOnSuccessListener;

    PauseCompletionSource(Executor executor, OnPausedListener<? super PState> pausedListener) {
        this.executor = executor;
        this.pResultOnSuccessListener = pausedListener;
    }

    @Override
    public void shout(@NonNull final ControllableTask<PState> pResultTask) {
        synchronized (waitObject) {
            if (executor == null || pResultOnSuccessListener == null)
                throw new NullPointerException("Executor & OnProgressListener must not be null!");

            if (pResultTask.isPaused()) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        pResultOnSuccessListener.OnPaused(pResultTask.getProgress());
                    }
                });
            }
        }
    }

    @Override
    public boolean maybeRemove(Object criteria) {
        synchronized (waitObject) {
            if (criteria instanceof OnPausedListener)
                if (criteria.equals(pResultOnSuccessListener))
                    return true;
            return false;
        }
    }
}
