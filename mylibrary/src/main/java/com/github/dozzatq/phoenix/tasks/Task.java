package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

public class Task<PResult> {
    private PResult taskResult;
    private Exception exception;
    private volatile Object taskTag;
    private volatile boolean isComplete;
    private volatile boolean isExcepted;
    private TaskListenerQueue<PResult> blockListenerSource = new TaskListenerQueue<>();
    protected final Object waitObject = new Object();

    public Task() {
        isComplete = false;
        isExcepted = false;
        blockListenerSource.keepSynced(true);
    }

    @CallSuper
    public boolean isComplete()
    {
        synchronized (waitObject) {
            return isComplete;
        }
    }

    @CallSuper
    public boolean isSuccessful()
    {
        synchronized (waitObject) {
            return isComplete && !isExcepted;
        }
    }

    @CallSuper
    public boolean isExcepted()
    {
        return isExcepted;
    }

    public void setResult(PResult pResult)
    {
        synchronized (waitObject) {
            isComplete = true;
            taskResult = pResult;
            notifyCompleteListeners();
        }
    }

    @CallSuper
    public Exception getException()
    {
        synchronized (waitObject) {
            return exception;
        }
    }

    @CallSuper
    public PResult getResult()
    {
        synchronized (waitObject) {
            if (isComplete())
                return taskResult;
            else return null;
        }
    }

    @CallSuper
    public void setException(Exception exception1)
    {
        synchronized (waitObject) {
            isExcepted = true;
            exception = exception1;
            notifyCompleteListeners();
        }
    }

    @NonNull
    public Task<PResult> addOnSuccessListener(@NonNull OnSuccessListener<PResult> listener)
    {
        return addOnSuccessListener(MainThreadExecutor.getInstance(), listener);
    }

    @NonNull
    public Task<PResult> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<PResult> listener)
    {
        synchronized (waitObject) {
            TaskQueueService<PResult> pResultTaskQueueService = new SuccessCompletionSource<>(executor, listener);
            blockListenerSource.addService(pResultTaskQueueService);
            if (isComplete())
                blockListenerSource.callForThis(pResultTaskQueueService, this);
            return this;
        }
    }

    @NonNull
    private Task<PResult> addOnExtensionListener(@NonNull OnExtensionListener<PResult> listener)
    {
        synchronized (waitObject) {
            TaskQueueService<PResult> pResultTaskQueueService =
                    new ExtensionCompletionSource<PResult>(MainThreadExecutor.getInstance(), listener);
            blockListenerSource.addService(pResultTaskQueueService);
            if (isComplete())
                blockListenerSource.callForThis(pResultTaskQueueService, this);
            return this;
        }
    }

    @NonNull
    public Task<PResult> addOnFailureListener(@NonNull OnFailureListener listener)
    {
        return addOnFailureListener(MainThreadExecutor.getInstance(), listener);
    }

    @NonNull
    public Task<PResult> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener listener)
    {
        synchronized (waitObject) {
            TaskQueueService<PResult> pResultTaskQueueService = new FailureCompletionSource<>(executor, listener);
            blockListenerSource.addService(pResultTaskQueueService);
            if (isExcepted())
                blockListenerSource.callForThis(pResultTaskQueueService, this);
            return this;
        }
    }

    public Task<PResult> removeOnCompleteListener(@NonNull OnCompleteListener<PResult> listener)
    {
        synchronized (waitObject) {
            blockListenerSource.removeFromQueue(listener);
            return this;
        }
    }

    public Task<PResult> removeOnFailureListener(@NonNull OnFailureListener listener)
    {
        synchronized (waitObject) {
            blockListenerSource.removeFromQueue(listener);
            return this;
        }
    }

    public Task<PResult> removeOnSuccessListener(@NonNull OnSuccessListener<PResult> listener)
    {
        synchronized (waitObject) {
            blockListenerSource.removeFromQueue(listener);
            return this;
        }
    }

    @NonNull
    public Task<PResult> addOnCompleteListener(@NonNull OnCompleteListener<PResult> listener)
    {
        return addOnCompleteListener(MainThreadExecutor.getInstance(), listener);
    }

    @NonNull
    public Task<PResult> addOnCompleteListener(@NonNull Executor executor, @NonNull OnCompleteListener<PResult> listener)
    {
        synchronized (waitObject) {
            TaskQueueService<PResult> pResultTaskQueueService = new CompleteCompletionSource<>(executor, listener);
            blockListenerSource.addService(pResultTaskQueueService);
            if (isComplete())
                blockListenerSource.callForThis(pResultTaskQueueService, this);
        }
        return this;
    }

    public <PUnion> Task<PResult> createUnionWith(Task<PUnion> unionTask, OnUnionListener<PResult, PUnion> unionListener)
    {
        return createUnionWith(MainThreadExecutor.getInstance(), unionTask, unionListener);
    }

    public <PUnion> Task<PResult> createUnionWith(Executor executor,Task<PUnion> unionTask, OnUnionListener<PResult, PUnion> unionListener)
    {
        synchronized (waitObject) {
            new UnionTask<PResult, PUnion>(executor,this, unionTask, unionListener);
            return this;
        }
    }

    public <PExtension> Task<PExtension> extensionWith(@NonNull Extension<PResult, PExtension> pExtension)
    {
        return extensionWith(MainThreadExecutor.getInstance(), pExtension);
    }

    public <PExtension> Task<PExtension> extensionWith(Executor executor, @NonNull Extension<PResult, PExtension> pExtension)
    {
        Task<PExtension> taskExtension = new Task<PExtension>();
        addOnExtensionListener(new ExtensionReviser<PResult, PExtension>(executor, pExtension, taskExtension));
        return taskExtension;
    }

    public <PExtension> Task<PExtension> extensionWithTask(@NonNull Extension<PResult, Task<PExtension>> pExtension)
    {
        return extensionWithTask(MainThreadExecutor.getInstance(), pExtension);
    }

    public <PExtension> Task<PExtension> extensionWithTask(Executor executor, @NonNull Extension<PResult, Task<PExtension>> pExtension)
    {
        Task<PExtension> taskExtension = new Task<PExtension>();
        addOnExtensionListener(new ExtensionReviserTask<PResult, PExtension>(executor, pExtension, taskExtension));
        return taskExtension;
    }

    private void notifyCompleteListeners()
    {
        blockListenerSource.callQueue(this);
    }

    public Object getTaskTag() {
        synchronized (waitObject) {
            return taskTag;
        }
    }

    public void setTaskTag(Object taskTag) {
        synchronized (waitObject) {
            this.taskTag = taskTag;
        }
    }

    public static <PResult> Task<PResult> fromSource(TaskSource<PResult> taskSource)
    {
        return Tasks.execute(taskSource);
    }

    public boolean isSynced() {
        synchronized (waitObject) {
            return blockListenerSource.isSynced();
        }
    }

    public Task<PResult> keepSynced(boolean keepSynced) {
        synchronized (waitObject) {
            blockListenerSource.keepSynced(keepSynced);
            return this;
        }
    }
}
