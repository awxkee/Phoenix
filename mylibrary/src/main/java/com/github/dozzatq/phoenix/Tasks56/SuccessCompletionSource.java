package com.github.dozzatq.phoenix.Tasks56;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by dxfb on 04.06.2017.
 */

class SuccessCompletionSource<PResult> implements TaskQueueService<PResult> {

    private Executor executor;
    private final Object waitObject=new Object();
    private OnSuccessListener<PResult> pResultOnSuccessListener;

    public SuccessCompletionSource(Executor executor, OnSuccessListener<PResult> pResultOnSuccessListener) {
        this.executor = executor;
        this.pResultOnSuccessListener = pResultOnSuccessListener;
    }

    @Override
    public void done(@NonNull final Task<PResult> pResultTask) {
        synchronized (waitObject)
        {
            if (executor==null || pResultOnSuccessListener==null)
                throw new NullPointerException("Executor & OnSuccessListener must not be null!");

            if (pResultTask.isSuccessful())
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
        synchronized (waitObject) {
            if (criteria instanceof OnSuccessListener)
                if (criteria.equals(pResultOnSuccessListener))
                    return true;
            return false;
        }
    }
}
