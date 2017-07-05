package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshyk on 10.06.2017.
 */

public abstract class CancellableTask<PState> extends Task<PState> {

    private CancellableListenerQueue<PState> stateCancellableListenerQueue;

    public CancellableTask()
    {
        stateCancellableListenerQueue = new CancellableListenerQueue<>();
    }

    public abstract boolean cancel();

    public abstract boolean isCanceled();

    public abstract boolean isInProgress();

    public abstract PState getProgress();

    public CancellableTask<PState> addOnProgressListener(@NonNull OnProgressListener<? super PState> listener)
    {
        return addOnProgressListener(MainThreadExecutor.getInstance(), listener);
    }

    public CancellableTask<PState> addOnProgressListener(@NonNull Executor executor,
                                                         @NonNull OnProgressListener<? super PState> listener)
    {
        synchronized (waitObject)
        {
            ProgressCompletionSource<PState> pStateProgressCompletionSource = new ProgressCompletionSource<PState>(executor, listener);
            stateCancellableListenerQueue.push(pStateProgressCompletionSource);
            if (isInProgress())
            {
                stateCancellableListenerQueue.callForThis(pStateProgressCompletionSource, this);
            }
            return this;
        }
    }

    public CancellableTask<PState> addOnCanceledListener(@NonNull OnCanceledListener<? super PState> onCanceledListener)
    {
        return addOnCanceledListener(MainThreadExecutor.getInstance(), onCanceledListener);
    }

    @CallSuper
    public void notifyChangedState()
    {
        synchronized (waitObject)
        {
            stateCancellableListenerQueue.callQueue(this);
        }
    }

    public CancellableTask<PState> addOnCanceledListener(@NonNull Executor executor,
                                                                  @NonNull OnCanceledListener<? super PState> onCanceledListener)
    {
        synchronized (waitObject){
            CancelCompletionSource<PState> cancelCompletionSource = new CancelCompletionSource<PState>(executor, onCanceledListener);
            stateCancellableListenerQueue.push(cancelCompletionSource);
            if (isCanceled() )
            {
                stateCancellableListenerQueue.callForThis(cancelCompletionSource, this);
            }
            return this;
        }
    }

    public CancellableTask<PState> removeOnCanceledListener(@NonNull OnCanceledListener<? super PState> onCanceledListener)
    {
        synchronized (waitObject) {
            stateCancellableListenerQueue.removeFromQueue(onCanceledListener);
            return this;
        }
    }

    public CancellableTask<PState> removeOnProgressListener(@NonNull OnProgressListener<? super PState> listener)
    {
        synchronized (waitObject) {
            stateCancellableListenerQueue.removeFromQueue(listener);
            return this;
        }
    }

    @Override
    public CancellableTask<PState> keepSynced(boolean keepSynced) {
        synchronized (waitObject) {
            stateCancellableListenerQueue.keepSynced(keepSynced);
        }
        return (CancellableTask<PState>) super.keepSynced(keepSynced);
    }
}
