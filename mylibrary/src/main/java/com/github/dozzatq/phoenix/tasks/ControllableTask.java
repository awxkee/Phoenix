package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshyk on 10.06.2017.
 */

public abstract class ControllableTask<PState> extends CancellableTask<PState> {

    public abstract boolean pause();

    public abstract boolean resume();

    public abstract boolean isPaused();

    public ControllableTask<PState> addOnPausedListener(@NonNull OnPausedListener<? super PState> onPausedListener)
    {
        return addOnPausedListener(MainThreadExecutor.getInstance(), onPausedListener, true);
    }

    public ControllableTask<PState> addOnPausedListener(@NonNull Executor executor,
                                                        @NonNull OnPausedListener<? super PState> onPausedListener, boolean keepSynced)
    {
        synchronized (mLock)
        {
            PauseCompletionSource<PState> pStatePauseCompletionSource = new PauseCompletionSource<>(executor, onPausedListener, keepSynced);
            push(pStatePauseCompletionSource);
            return this;
        }
    }

    public ControllableTask<PState> removeOnPausedListener(@NonNull OnPausedListener<? super PState> onPausedListener)
    {
        cropQueue(onPausedListener);
        return this;
    }


}
