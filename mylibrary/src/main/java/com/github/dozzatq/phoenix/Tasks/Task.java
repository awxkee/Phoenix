package com.github.dozzatq.phoenix.Tasks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

public class Task<PResult> {
    private PResult taskResult;
    private Exception exception;
    private volatile Object taskTag;
    private volatile boolean isComplete;
    private volatile boolean isExcepted;
    private TaskListenerQueue<PResult> blockListenerSource = new TaskListenerQueue<>();
    protected final Object synchronizedObject = new Object();

    public Task() {
        isComplete = false;
        isExcepted = false;
        blockListenerSource.keepSynced(true);
    }

    public boolean isComplete()
    {
        synchronized (synchronizedObject) {
            return isComplete;
        }
    }

    public boolean isSuccessful()
    {
        synchronized (synchronizedObject) {
            return isComplete && !isExcepted;
        }
    }

    public boolean isExcepted()
    {
        return isExcepted;
    }

    public void setResult(PResult pResult)
    {
        synchronized (synchronizedObject) {
            isComplete = true;
            taskResult = pResult;
            notifyCompleteListeners();
        }
    }

    public Exception getException()
    {
        synchronized (synchronizedObject) {
            return exception;
        }
    }

    public PResult getResult()
    {
        synchronized (synchronizedObject) {
            if (isComplete())
                return taskResult;
            else return null;
        }
    }

    public void setException(Exception exception1)
    {
        synchronized (synchronizedObject) {
            isExcepted = true;
            exception = exception1;
            notifyCompleteListeners();
        }
    }

    @NonNull
    public Task<PResult> addOnSuccessListener(@NonNull OnSuccessListener<PResult> listener)
    {
        return addOnSuccessListener(DefaultExecutor.getInstance(), listener);
    }

    @NonNull
    public Task<PResult> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<PResult> listener)
    {
        synchronized (synchronizedObject) {
            OnTaskCompleteListener<PResult> pResultOnTaskCompleteListener = new SuccessCompletionSource<>(executor, listener);
            blockListenerSource.addService(pResultOnTaskCompleteListener);
            if (isComplete())
                blockListenerSource.callForThis(pResultOnTaskCompleteListener, this);
            return this;
        }
    }

    @NonNull
    private Task<PResult> addOnExtensionListener(@NonNull OnExtensionListener<PResult> listener)
    {
        synchronized (synchronizedObject) {
            OnTaskCompleteListener<PResult> pResultOnTaskCompleteListener = new ExtensionCompletionSource<PResult>(DefaultExecutor.getInstance(), listener);
            blockListenerSource.addService(pResultOnTaskCompleteListener);
            if (isComplete())
                blockListenerSource.callForThis(pResultOnTaskCompleteListener, this);
            return this;
        }
    }

    @NonNull
    public Task<PResult> addOnFailureListener(@NonNull OnFailureListener listener)
    {
        return addOnFailureListener(DefaultExecutor.getInstance(), listener);
    }

    @NonNull
    public Task<PResult> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener listener)
    {
        synchronized (synchronizedObject) {
            OnTaskCompleteListener<PResult> pResultOnTaskCompleteListener = new FailureCompletionSource<>(executor, listener);
            blockListenerSource.addService(pResultOnTaskCompleteListener);
            if (isExcepted())
                blockListenerSource.callForThis(pResultOnTaskCompleteListener, this);
            return this;
        }
    }


 /*   public Task<PResult> removeOnCompleteListener(@NonNull OnCompleteListener<PResult> listener)
    {
        synchronized (synchronizedObject) {
            if (onCompleteListeners.contains(listener))
                onCompleteListeners.remove(listener);
            return this;
        }
    }

    public Task<PResult> removeOnFailureListener(@NonNull OnFailureListener listener)
    {
        synchronized (synchronizedObject) {
            if (onFailureListeners.contains(listener))
                onFailureListeners.remove(listener);
            return this;
        }
    }

    public Task<PResult> removeOnSuccessListener(@NonNull OnSuccessListener<PResult> listener)
    {
        synchronized (synchronizedObject) {
            if (onSuccessListeners.contains(listener))
                onSuccessListeners.remove(listener);
            return this;
        }
    }*/

    @NonNull
    public Task<PResult> addOnCompleteListener(@NonNull OnCompleteListener<PResult> listener)
    {
        return addOnCompleteListener(DefaultExecutor.getInstance(), listener);
    }

    @NonNull
    public Task<PResult> addOnCompleteListener(@NonNull Executor executor, @NonNull OnCompleteListener<PResult> listener)
    {
        synchronized (synchronizedObject) {
            OnTaskCompleteListener<PResult> pResultOnTaskCompleteListener = new CompleteCompletionSource<>(DefaultExecutor.getInstance(), listener);
            blockListenerSource.addService(pResultOnTaskCompleteListener);
            if (isComplete())
                blockListenerSource.callForThis(pResultOnTaskCompleteListener, this);
        }
        return this;
    }

    public <PUnion> Task<PResult> createUnionWith(Task<PUnion> unionTask, OnUnionListener<PResult, PUnion> unionListener)
    {
        return createUnionWith(DefaultExecutor.getInstance(), unionTask, unionListener);
    }

    public <PUnion> Task<PResult> createUnionWith(Executor executor,Task<PUnion> unionTask, OnUnionListener<PResult, PUnion> unionListener)
    {
        synchronized (synchronizedObject) {
            new UnionTask<PResult, PUnion>(executor,this, unionTask, unionListener);
            return this;
        }
    }

    public <PExtension> Task<PExtension> extensionWith(@NonNull Extension<PResult, PExtension> pExtension)
    {
        return extensionWith(DefaultExecutor.getInstance(), pExtension);
    }

    public <PExtension> Task<PExtension> extensionWith(Executor executor, @NonNull Extension<PResult, PExtension> pExtension)
    {
        Task<PExtension> taskExtension = new Task<PExtension>();
        addOnExtensionListener(new ExtensionReviser<PResult, PExtension>(executor, pExtension, taskExtension));
        return taskExtension;
    }

    public <PExtension> Task<PExtension> extensionWithTask(@NonNull Extension<PResult, Task<PExtension>> pExtension)
    {
        return extensionWithTask(DefaultExecutor.getInstance(), pExtension);
    }

    public <PExtension> Task<PExtension> extensionWithTask(Executor executor, @NonNull Extension<PResult, Task<PExtension>> pExtension)
    {
        Task<PExtension> taskExtension = new Task<PExtension>();
        addOnExtensionListener(new ExtensionReviserTask<PResult, PExtension>(executor, pExtension, taskExtension));
        return taskExtension;
    }

    private void notifyCompleteListeners()
    {
        blockListenerSource.callComplete(this);
    }

    public Object getTaskTag() {
        synchronized (synchronizedObject) {
            return taskTag;
        }
    }

    public void setTaskTag(Object taskTag) {
        synchronized (synchronizedObject) {
            this.taskTag = taskTag;
        }
    }

    public static final <PResult> Task<PResult> fromSource(TaskSource<PResult> taskSource)
    {
        return Tasks.execute(taskSource);
    }

    public boolean isSynced() {
        synchronized (synchronizedObject) {
            return blockListenerSource.isSynced();
        }
    }

    public Task<PResult> keepSynced(boolean keepSynced) {
        synchronized (synchronizedObject) {
            blockListenerSource.keepSynced(keepSynced);
            return this;
        }
    }
}
