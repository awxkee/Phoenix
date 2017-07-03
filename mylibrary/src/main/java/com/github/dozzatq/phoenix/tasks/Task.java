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
    private TaskListenerQueue<PResult> queueListeners = new TaskListenerQueue<>();
    protected final Object waitObject = new Object();

    public Task() {
        isComplete = false;
        isExcepted = false;
        queueListeners.keepSynced(true);
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
            notifyListeners();
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
            notifyListeners();
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
            queueListeners.addService(pResultTaskQueueService);
            if (isSuccessful())
                queueListeners.callForThis(pResultTaskQueueService, this);
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
            queueListeners.addService(pResultTaskQueueService);
            if (isExcepted())
                queueListeners.callForThis(pResultTaskQueueService, this);
            return this;
        }
    }

    @NonNull
    public Task<PResult> addOnTaskSuccessListener(@NonNull OnTaskSuccessListener<PResult> listener)
    {
        return addOnTaskSuccessListener(MainThreadExecutor.getInstance(), listener);
    }

    @NonNull
    public Task<PResult> addOnTaskSuccessListener(@NonNull Executor executor, @NonNull OnTaskSuccessListener<PResult> listener)
    {
        synchronized (waitObject) {
            TaskQueueService<PResult> pResultTaskQueueService = new TaskSuccessCompletionSource<>(executor, listener);
            queueListeners.addService(pResultTaskQueueService);
            if (isSuccessful())
                queueListeners.callForThis(pResultTaskQueueService, this);
            return this;
        }
    }

    @NonNull
    public Task<PResult> addOnTaskFailureListener(@NonNull OnTaskFailureListener<PResult> listener)
    {
        return addOnTaskFailureListener(MainThreadExecutor.getInstance(), listener);
    }

    @NonNull
    public Task<PResult> addOnTaskFailureListener(@NonNull Executor executor, @NonNull OnTaskFailureListener<PResult> listener)
    {
        synchronized (waitObject) {
            TaskQueueService<PResult> pResultTaskQueueService = new TaskFailureCompletionSource<>(executor, listener);
            queueListeners.addService(pResultTaskQueueService);
            if (isExcepted())
                queueListeners.callForThis(pResultTaskQueueService, this);
            return this;
        }
    }

    public Task<PResult> removeOnTaskSuccessListener(@NonNull OnTaskSuccessListener<PResult> listener)
    {
        synchronized (waitObject) {
            queueListeners.removeFromQueue(listener);
            return this;
        }
    }

    public Task<PResult> removeOnTaskFailureListener(@NonNull OnTaskFailureListener<PResult> listener)
    {
        synchronized (waitObject) {
            queueListeners.removeFromQueue(listener);
            return this;
        }
    }

    public Task<PResult> removeOnCompleteListener(@NonNull OnCompleteListener<PResult> listener)
    {
        synchronized (waitObject) {
            queueListeners.removeFromQueue(listener);
            return this;
        }
    }

    public Task<PResult> removeOnFailureListener(@NonNull OnFailureListener listener)
    {
        synchronized (waitObject) {
            queueListeners.removeFromQueue(listener);
            return this;
        }
    }

    public Task<PResult> removeOnSuccessListener(@NonNull OnSuccessListener<PResult> listener)
    {
        synchronized (waitObject) {
            queueListeners.removeFromQueue(listener);
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
            queueListeners.addService(pResultTaskQueueService);
            if (isComplete())
                queueListeners.callForThis(pResultTaskQueueService, this);
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
        addOnTaskSuccessListener(new ExtensionReviser<PResult, PExtension>(executor, pExtension, taskExtension));
        return taskExtension;
    }

    public <PExtension> Task<PExtension> extensionWithTask(@NonNull Extension<PResult, Task<PExtension>> pExtension)
    {
        return extensionWithTask(MainThreadExecutor.getInstance(), pExtension);
    }

    public <PExtension> Task<PExtension> extensionWithTask(Executor executor, @NonNull Extension<PResult, Task<PExtension>> pExtension)
    {
        Task<PExtension> taskExtension = new Task<PExtension>();
        addOnTaskSuccessListener(new ExtensionReviserTask<PResult, PExtension>(executor, pExtension, taskExtension));
        return taskExtension;
    }

    private void notifyListeners()
    {
        queueListeners.callQueue(this);
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
            return queueListeners.isSynced();
        }
    }

    public Task<PResult> keepSynced(boolean keepSynced) {
        synchronized (waitObject) {
            queueListeners.keepSynced(keepSynced);
            return this;
        }
    }
}
