package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshyk on 10.06.2017.
 */

class PauseCompletionSource<PState> implements TaskQueueService<PState> {

    private Executor executor;
    private final Object mLock = new Object();
    private OnPausedListener<? super PState> pResultOnSuccessListener;
    private boolean keepSynced;

    PauseCompletionSource(Executor executor, OnPausedListener<? super PState> pausedListener, boolean keepSynced) {
        this.executor = executor;
        this.pResultOnSuccessListener = pausedListener;
        this.keepSynced = keepSynced;
    }

    @Override
    public void sync(@NonNull Task<PState> pStateTask) {

        if (!(pStateTask instanceof ControllableTask))
            return;

        synchronized (mLock) {

            if (pResultOnSuccessListener==null)
                return;

            if (executor == null)
                throw new NullPointerException("Executor must not be null!");

            final ControllableTask<PState> controllableTask = (ControllableTask<PState>) pStateTask;

            if (needSync(controllableTask)) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        pResultOnSuccessListener.OnPaused(controllableTask.getProgress());
                    }
                });
            }
        }
    }

    @Override
    public boolean maybeRemove(Object criteria) {
        synchronized (mLock) {
            return criteria instanceof OnPausedListener && criteria.equals(pResultOnSuccessListener);
        }
    }

    @Override
    public boolean needSync(@NonNull Task<PState> pStateTask) {

        if (!(pStateTask instanceof ControllableTask))
            return false;
        ControllableTask<PState> controllableTask = (ControllableTask<PState>) pStateTask;
        return controllableTask.isPaused();
    }

    @Override
    public boolean isKeepSynced() {
        synchronized (mLock) {
            return keepSynced;
        }
    }
}
