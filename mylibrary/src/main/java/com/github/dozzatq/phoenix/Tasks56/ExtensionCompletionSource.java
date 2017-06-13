package com.github.dozzatq.phoenix.Tasks56;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by dxfb on 04.06.2017.
 */

class ExtensionCompletionSource<PResult> implements TaskQueueService<PResult> {

    private Executor executor;
    private final Object waitObject=new Object();
    private OnExtensionListener<PResult> pResultOnExtensionListener;

    public ExtensionCompletionSource(Executor executor, OnExtensionListener<PResult> pResultOnSuccessListener) {
        this.executor = executor;
        this.pResultOnExtensionListener = pResultOnSuccessListener;
    }

    @Override
    public void done(@NonNull final Task<PResult> pResultTask) {
        synchronized (waitObject)
        {
            if (executor==null || pResultOnExtensionListener==null)
                throw new NullPointerException("Executor & OnExtensionListener must not be null!");

            if (pResultTask.isComplete())
            {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        pResultOnExtensionListener.OnExtension(pResultTask);
                    }
                });
            }
        }
    }

    @Override
    public boolean maybeRemove(Object criteria) {
        synchronized (waitObject) {
            if (criteria instanceof OnExtensionListener)
                if (criteria.equals(pResultOnExtensionListener))
                    return true;
            return false;
        }
    }
}
