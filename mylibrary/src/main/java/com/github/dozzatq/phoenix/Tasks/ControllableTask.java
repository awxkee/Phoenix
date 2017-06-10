package com.github.dozzatq.phoenix.Tasks;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by dxfb on 10.06.2017.
 */

public abstract class ControllableTask<PState> extends CancellableTask<PState> {

    private ControlListenerQueue<PState> controlListenerQueue;

    public ControllableTask()
    {
        controlListenerQueue = new ControlListenerQueue<PState>();
    }

    public abstract boolean pause();

    public abstract boolean resume();

    public abstract boolean isPaused();

    @CallSuper
    public void notifyControlChanged()
    {
        synchronized (waitObject)
        {
            controlListenerQueue.callQueue(this);
        }
    }

    public ControllableTask<PState> addOnPausedListener(@NonNull OnPausedListener<? super PState> onPausedListener)
    {
        return addOnPausedListener(DefaultExecutor.getInstance(), onPausedListener);
    }

    public ControllableTask<PState> addOnPausedListener(@NonNull Executor executor,
                                                        @NonNull OnPausedListener<? super PState> onPausedListener)
    {
        synchronized (waitObject)
        {
            PauseCompletionSource<PState> pStatePauseCompletionSource = new PauseCompletionSource<>(executor, onPausedListener);
            controlListenerQueue.push(pStatePauseCompletionSource);
            if (isPaused())
            {
                controlListenerQueue.callForThis(pStatePauseCompletionSource, this);
            }
            return this;
        }
    }

    public ControllableTask<PState> removeOnPausedListener(@NonNull OnPausedListener<? super PState> onPausedListener)
    {
        synchronized (waitObject)
        {
            controlListenerQueue.removeFromQueue(onPausedListener);
            return this;
        }
    }

    @Override
    public ControllableTask<PState> keepSynced(boolean keepSynced) {
        synchronized (waitObject) {
            controlListenerQueue.keepSynced(keepSynced);
        }
        return (ControllableTask<PState>) super.keepSynced(keepSynced);
    }
}
