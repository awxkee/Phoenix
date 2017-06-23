package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by dxfb on 23.06.2017.
 */

class TaskSuccessCompletionSource<PResult> implements TaskQueueService<PResult> {

    private Executor executor;
    private OnTaskSuccessListener<PResult> onTaskSuccessListener;
    private final Object waitObject = new Object();

    TaskSuccessCompletionSource(Executor executor, OnTaskSuccessListener<PResult> onTaskSuccessListener) {
        this.executor = executor;
        this.onTaskSuccessListener = onTaskSuccessListener;
    }

    @Override
    public void done(@NonNull final Task<PResult> task) {
        synchronized (waitObject) {

            if (onTaskSuccessListener==null)
                return;

            if (executor == null)
                throw new NullPointerException("Executor in task must not be null!");

            if (task.isSuccessful()) {
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
        synchronized (waitObject) {
            if (criteria instanceof OnProgressListener)
                if (criteria.equals(onTaskSuccessListener))
                    return true;
            return false;
        }
    }
}
