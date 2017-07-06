package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshyk on 10.06.2017.
 */

public abstract class CancellableTask<PState> extends Task<PState> {

    public abstract boolean cancel();

    public abstract boolean isCanceled();

    public abstract boolean isInProgress();

    public abstract PState getProgress();

    public CancellableTask<PState> addOnProgressListener(@NonNull OnProgressListener<? super PState> listener)
    {
        return addOnProgressListener(MainThreadExecutor.getInstance(), listener, true);
    }

    public CancellableTask<PState> addOnProgressListener(@NonNull Executor executor,
                                                         @NonNull OnProgressListener<? super PState> listener, boolean keepSynced)
    {
        synchronized (mLock)
        {
            ProgressCompletionSource<PState> pStateProgressCompletionSource = new ProgressCompletionSource<PState>(executor, listener, keepSynced);
            push(pStateProgressCompletionSource);
            return this;
        }
    }

    public CancellableTask<PState> addOnCanceledListener(@NonNull OnCanceledListener<? super PState> onCanceledListener)
    {
        return addOnCanceledListener(MainThreadExecutor.getInstance(), onCanceledListener, false);
    }

    public CancellableTask<PState> addOnCanceledListener(@NonNull Executor executor,
                                                                  @NonNull OnCanceledListener<? super PState> onCanceledListener, boolean keepSynced)
    {
        synchronized (mLock){
            CancelCompletionSource<PState> cancelCompletionSource = new CancelCompletionSource<PState>(executor, onCanceledListener, keepSynced);
            push(cancelCompletionSource);
            return this;
        }
    }

    public CancellableTask<PState> removeOnCanceledListener(@NonNull OnCanceledListener<? super PState> onCanceledListener)
    {
        synchronized (mLock) {
            cropQueue(onCanceledListener);
            return this;
        }
    }

    public CancellableTask<PState> removeOnProgressListener(@NonNull OnProgressListener<? super PState> listener)
    {
        synchronized (mLock) {
            cropQueue(listener);
            return this;
        }
    }

}
