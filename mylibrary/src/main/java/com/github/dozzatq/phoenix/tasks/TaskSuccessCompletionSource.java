package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshik on 23.06.2017.
 */

class TaskSuccessCompletionSource<PResult> implements TaskQueueService<PResult> {

    private Executor executor;
    private OnTaskSuccessListener<PResult> onTaskSuccessListener;
    private final Object mLock = new Object();
    private boolean keepSynced;

    TaskSuccessCompletionSource(Executor executor, OnTaskSuccessListener<PResult> onTaskSuccessListener, boolean keepSynced) {
        this.executor = executor;
        this.onTaskSuccessListener = onTaskSuccessListener;
        this.keepSynced = keepSynced;
    }

    @Override
    public void sync(@NonNull final Task<PResult> task) {
        synchronized (mLock) {

            if (onTaskSuccessListener==null)
                return;

            if (executor == null)
                throw new NullPointerException("Executor in task must not be null!");

            if (needSync(task)) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        onTaskSuccessListener.OnTaskSuccess(task);
                    }
                });
            }
        }
    }

    @Override
    public boolean maybeRemove(Object criteria) {
        synchronized (mLock) {
            return criteria instanceof OnProgressListener && criteria.equals(onTaskSuccessListener);
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
