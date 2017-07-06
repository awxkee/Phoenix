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
    protected final Object mLock = new Object();

    public Task() {
        isComplete = false;
        isExcepted = false;
    }

    @CallSuper
    public boolean isComplete()
    {
        synchronized (mLock) {
            return isComplete;
        }
    }

    @CallSuper
    public boolean isSuccessful()
    {
        synchronized (mLock) {
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
        synchronized (mLock) {
            isComplete = true;
            taskResult = pResult;
            notifyListeners();
        }
    }

    @CallSuper
    public Exception getException()
    {
        synchronized (mLock) {
            return exception;
        }
    }

    @CallSuper
    public PResult getResult()
    {
        synchronized (mLock) {
            if (isComplete())
                return taskResult;
            else return null;
        }
    }

    @CallSuper
    public void setException(Exception exception1)
    {
        synchronized (mLock) {
            isExcepted = true;
            exception = exception1;
            notifyListeners();
        }
    }

    @NonNull
    public Task<PResult> addOnSuccessListener(@NonNull OnSuccessListener<PResult> listener)
    {
        return addOnSuccessListener(MainThreadExecutor.getInstance(), listener, true);
    }

    @NonNull
    public Task<PResult> addOnSuccessListener(@NonNull OnSuccessListener<PResult> listener, boolean keepSynced)
    {
        return addOnSuccessListener(MainThreadExecutor.getInstance(), listener, keepSynced);
    }

    @NonNull
    public Task<PResult> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<PResult> listener)
    {
        return addOnSuccessListener(executor, listener, true);
    }

    @NonNull
    public Task<PResult> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<PResult> listener, boolean keepSynced)
    {
        synchronized (mLock) {
            TaskQueueService<PResult> pResultTaskQueueService = new SuccessCompletionSource<>(executor, listener, keepSynced);
            queueListeners.addService(pResultTaskQueueService, this);
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
        return addOnFailureListener(executor, listener, true);
    }

    @NonNull
    public Task<PResult> addOnFailureListener(@NonNull OnFailureListener listener, boolean keepSynced)
    {
        return addOnFailureListener(MainThreadExecutor.getInstance(), listener, keepSynced);
    }

    @NonNull
    public Task<PResult> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener listener, boolean keepSynced)
    {
        synchronized (mLock) {
            TaskQueueService<PResult> pResultTaskQueueService = new FailureCompletionSource<>(executor, listener, keepSynced);
            queueListeners.addService(pResultTaskQueueService, this);
            return this;
        }
    }

    @NonNull
    public Task<PResult> addOnTaskSuccessListener(@NonNull OnTaskSuccessListener<PResult> listener)
    {
        return addOnTaskSuccessListener(MainThreadExecutor.getInstance(), listener);
    }

    @NonNull
    public Task<PResult> addOnTaskSuccessListener(@NonNull OnTaskSuccessListener<PResult> listener, boolean keepSynced)
    {
        return addOnTaskSuccessListener(MainThreadExecutor.getInstance(), listener, keepSynced);
    }

    @NonNull
    public Task<PResult> addOnTaskSuccessListener(@NonNull Executor executor, @NonNull OnTaskSuccessListener<PResult> listener)
    {
        return addOnTaskSuccessListener(executor, listener, true);
    }

    @NonNull
    public Task<PResult> addOnTaskSuccessListener(@NonNull Executor executor, @NonNull OnTaskSuccessListener<PResult> listener, boolean keepSynced)
    {
        synchronized (mLock) {
            TaskQueueService<PResult> pResultTaskQueueService = new TaskSuccessCompletionSource<>(executor, listener, keepSynced);
            queueListeners.addService(pResultTaskQueueService, this);
            return this;
        }
    }

    @NonNull
    public Task<PResult> addOnTaskFailureListener(@NonNull OnTaskFailureListener<PResult> listener)
    {
        return addOnTaskFailureListener(MainThreadExecutor.getInstance(), listener);
    }

    @NonNull
    public Task<PResult> addOnTaskFailureListener(@NonNull OnTaskFailureListener<PResult> listener, boolean keepSynced)
    {
        return addOnTaskFailureListener(MainThreadExecutor.getInstance(), listener, keepSynced);
    }

    @NonNull
    public Task<PResult> addOnTaskFailureListener(@NonNull Executor executor, @NonNull OnTaskFailureListener<PResult> listener)
    {
        return addOnTaskFailureListener(executor, listener, true);
    }

    @NonNull
    public Task<PResult> addOnTaskFailureListener(@NonNull Executor executor, @NonNull OnTaskFailureListener<PResult> listener, boolean keepSynced)
    {
        synchronized (mLock) {
            TaskQueueService<PResult> pResultTaskQueueService = new TaskFailureCompletionSource<>(executor, listener, keepSynced);
            queueListeners.addService(pResultTaskQueueService, this);
            return this;
        }
    }

    @NonNull
    public Task<PResult> removeOnTaskSuccessListener(@NonNull OnTaskSuccessListener<PResult> listener)
    {
        synchronized (mLock) {
            queueListeners.removeFromQueue(listener);
            return this;
        }
    }

    @NonNull
    public Task<PResult> removeOnTaskFailureListener(@NonNull OnTaskFailureListener<PResult> listener)
    {
        synchronized (mLock) {
            queueListeners.removeFromQueue(listener);
            return this;
        }
    }

    @NonNull
    public Task<PResult> removeOnCompleteListener(@NonNull OnCompleteListener<PResult> listener)
    {
        synchronized (mLock) {
            queueListeners.removeFromQueue(listener);
            return this;
        }
    }

    @NonNull
    public Task<PResult> removeOnFailureListener(@NonNull OnFailureListener listener)
    {
        synchronized (mLock) {
            queueListeners.removeFromQueue(listener);
            return this;
        }
    }

    @NonNull
    public Task<PResult> removeOnSuccessListener(@NonNull OnSuccessListener<PResult> listener)
    {
        synchronized (mLock) {
            queueListeners.removeFromQueue(listener);
            return this;
        }
    }

    @NonNull
    public Task<PResult> addOnCompleteListener(@NonNull OnCompleteListener<PResult> listener)
    {
        return addOnCompleteListener(MainThreadExecutor.getInstance(), listener, true);
    }

    @NonNull
    public Task<PResult> addOnCompleteListener(@NonNull OnCompleteListener<PResult> listener, boolean keepSynced)
    {
        return addOnCompleteListener(MainThreadExecutor.getInstance(), listener, keepSynced);
    }

    @NonNull
    public Task<PResult> addOnCompleteListener(@NonNull Executor executor, @NonNull OnCompleteListener<PResult> listener)
    {
        return addOnCompleteListener(executor, listener, true);
    }

    @NonNull
    public Task<PResult> addOnCompleteListener(@NonNull Executor executor, @NonNull OnCompleteListener<PResult> listener, boolean keepSynced)
    {
        synchronized (mLock) {
            TaskQueueService<PResult> pResultTaskQueueService = new CompleteCompletionSource<>(executor, listener, keepSynced);
            queueListeners.addService(pResultTaskQueueService, this);
        }
        return this;
    }

    void push(TaskQueueService<PResult> sameInterface)
    {
        synchronized (mLock) {
            queueListeners.addService(sameInterface, this);
        }
    }

    void cropQueue(Object object)
    {
        synchronized (mLock)
        {
            queueListeners.removeFromQueue(object);
        }
    }

    public <PUnion> Task<PResult> createUnionWith(Task<PUnion> unionTask, OnUnionListener<PResult, PUnion> unionListener)
    {
        return createUnionWith(MainThreadExecutor.getInstance(), unionTask, unionListener);
    }

    public <PUnion> Task<PResult> createUnionWith(Executor executor,Task<PUnion> unionTask, OnUnionListener<PResult, PUnion> unionListener)
    {
        synchronized (mLock) {
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
        addOnTaskSuccessListener(new ExtensionReviser<PResult, PExtension>(executor, pExtension, taskExtension), false);
        return taskExtension;
    }

    public <PExtension> Task<PExtension> extensionWithTask(@NonNull Extension<PResult, Task<PExtension>> pExtension)
    {
        return extensionWithTask(MainThreadExecutor.getInstance(), pExtension);
    }

    public <PExtension> Task<PExtension> extensionWithTask(Executor executor, @NonNull Extension<PResult, Task<PExtension>> pExtension)
    {
        Task<PExtension> taskExtension = new Task<PExtension>();
        addOnTaskSuccessListener(new ExtensionReviserTask<PResult, PExtension>(executor, pExtension, taskExtension), false);
        return taskExtension;
    }

    void notifyListeners()
    {
        queueListeners.callQueue(this);
    }

    public Object getTaskTag() {
        synchronized (mLock) {
            return taskTag;
        }
    }

    public void setTaskTag(Object taskTag) {
        synchronized (mLock) {
            this.taskTag = taskTag;
        }
    }

    public static <PResult> Task<PResult> fromSource(TaskSource<PResult> taskSource)
    {
        return Tasks.execute(taskSource);
    }

}
