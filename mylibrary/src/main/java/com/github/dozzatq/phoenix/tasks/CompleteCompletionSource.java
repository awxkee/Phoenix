package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshyk on 04.06.2017.
 */

class CompleteCompletionSource<PResult> implements TaskQueueService<PResult> {

    private Executor executor;
    private final Object waitObject=new Object();
    private OnCompleteListener<PResult> pResultOnCompleteListener;

    public CompleteCompletionSource(Executor executor, OnCompleteListener<PResult> pResultOnCompleteListener) {
        this.executor = executor;
        this.pResultOnCompleteListener = pResultOnCompleteListener;
    }


    @Override
    public void done(@NonNull final Task<PResult> pResultTask) {
        synchronized (waitObject)
        {
            if (executor==null || pResultOnCompleteListener==null)
                throw new NullPointerException("Executor & OnCompleteListener must not be null!");

            if (pResultTask.isComplete())
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
        synchronized (waitObject) {
            if (criteria instanceof OnCompleteListener)
                if (criteria.equals(pResultOnCompleteListener))
                    return true;
            return false;
        }
    }
}
