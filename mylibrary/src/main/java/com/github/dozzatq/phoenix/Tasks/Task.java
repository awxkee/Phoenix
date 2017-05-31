package com.github.dozzatq.phoenix.Tasks;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.Executor;

public class Task<PResult> {
    private PResult taskResult;
    private Exception exception;
    private ArrayDeque<OnSuccessListener<PResult>> onSuccessListeners;
    private ArrayDeque<OnFailureListener> onFailureListeners;
    private ArrayDeque<OnCompleteListener<PResult>> onCompleteListeners;
    private ArrayDeque<OnExtensionListener<PResult>> onExtensionListeners;
    private volatile Object taskTag;
    private volatile boolean isComplete;
    private volatile boolean isExcepted;
    protected Handler handler;
    private Executor executor;
    private volatile boolean keepSynced;
    protected final Object synchronizedObject = new Object();

    public Task() {
        onSuccessListeners = new ArrayDeque<>();
        onFailureListeners = new ArrayDeque<>();
        onCompleteListeners = new ArrayDeque<>();
        handler = new Handler();
        isComplete = false;
        isExcepted = false;
        keepSynced = true;
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
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyCompleteListeners();
                    notifySuccessListeners();
                }
            });
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
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyFailureListener();
                }
            });
        }
    }

    @NonNull
    public Task<PResult> addOnSuccessListener(@NonNull OnSuccessListener<PResult> listener)
    {
        synchronized (synchronizedObject) {
            onSuccessListeners.add(listener);
            if (getResult() != null) {
                try {
                    listener.OnSuccess(getResult());
                    if (!isSynced())
                        removeOnSuccessListener(listener);
                } catch (Exception e) {
                    Log.d("Task<PResult>", "Bad Success Listener");
                }
            }
            return this;
        }
    }

    @NonNull
    private Task<PResult> addOnExtensionListener(@NonNull OnExtensionListener<PResult> listener)
    {
        synchronized (synchronizedObject) {
            if (onExtensionListeners==null)
                onExtensionListeners = new ArrayDeque<>();
            onExtensionListeners.add(listener);
            if (isComplete())
            {
                try{
                    listener.OnExtension(this);}
                catch (Exception e){
                    Log.d("Task<PResult>", "Bad Extension Listener");
                }
            }
            return this;
        }
    }

    public Task<PResult> removeOnCompleteListener(@NonNull OnCompleteListener<PResult> listener)
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
    }

    @NonNull
    public Task<PResult> addOnCompleteListener(@NonNull OnCompleteListener<PResult> listener)
    {
        synchronized (synchronizedObject) {
            onCompleteListeners.add(listener);
            if (isComplete()) {
                try {
                    listener.OnComplete(getResult());
                    if (!isSynced())
                        removeOnCompleteListener(listener);
                } catch (Exception e) {
                    Log.d("Task<PResult>", "Bad Complete Listener");
                }
            }
        }
        return this;
    }

    public <PUnion> Task<PResult> createUnionWith(Task<PUnion> unionTask, OnUnionListener<PResult, PUnion> unionListener)
    {
        synchronized (synchronizedObject) {
            new UnionTask<PResult, PUnion>(this, unionTask, unionListener);
            return this;
        }
    }

    public <PExtension> Task<PExtension> extensionWith(@NonNull Extension<PResult, PExtension> pExtension)
    {
        Task<PExtension> taskExtension = new Task<PExtension>();
        addOnExtensionListener(new ExtensionReviser<PResult, PExtension>(executor, pExtension, taskExtension));
        return taskExtension;
    }

    public <PExtension> Task<PExtension> extensionWith(Executor executor,@NonNull Extension<PResult, PExtension> pExtension)
    {
        Task<PExtension> taskExtension = new Task<PExtension>();
        addOnExtensionListener(new ExtensionReviser<PResult, PExtension>(executor, pExtension, taskExtension));
        return taskExtension;
    }

    public <PExtension> Task<PExtension> extensionWithTask(@NonNull Extension<PResult, Task<PExtension>> pExtension)
    {
        Task<PExtension> taskExtension = new Task<PExtension>();
        addOnExtensionListener(new ExtensionReviserTask<PResult, PExtension>(executor, pExtension, taskExtension));
        return taskExtension;
    }

    public <PExtension> Task<PExtension> extensionWithTask(Executor executor, @NonNull Extension<PResult, Task<PExtension>> pExtension)
    {
        Task<PExtension> taskExtension = new Task<PExtension>();
        addOnExtensionListener(new ExtensionReviserTask<PResult, PExtension>(executor, pExtension, taskExtension));
        return taskExtension;
    }

    @NonNull
    public Task<PResult> addOnFailureListener(@NonNull OnFailureListener listener)
    {
        synchronized (synchronizedObject) {
            onFailureListeners.add(listener);
            if (isExcepted()) {
                try {
                    listener.OnFailure(getException());
                    if (!isSynced())
                        removeOnFailureListener(listener);
                } catch (Exception e) {
                    Log.d("Task<PResult>", "Bad Failure Listener");
                }
            }
            return this;
        }
    }

    private void notifySuccessListeners()
    {
        if (isSuccessful()) {
            synchronized (synchronizedObject) {

                Iterator<OnSuccessListener<PResult>> iterator = onSuccessListeners.descendingIterator();
                while (iterator.hasNext())
                {
                    OnSuccessListener<PResult> listener = iterator.next();
                    try {
                        listener.OnSuccess(getResult());
                    } catch (Exception e) {
                        Log.d("Task<PResult>", "Bad Success Listener");
                    }
                    if (!isSynced())
                        iterator.remove();
                }

                if (onExtensionListeners!=null) {
                    Iterator<OnExtensionListener<PResult>> iteratorExtension = onExtensionListeners.descendingIterator();
                    while (iteratorExtension.hasNext()) {
                        OnExtensionListener<PResult> listener = iteratorExtension.next();
                        try {
                            listener.OnExtension(this);
                        } catch (Exception e) {
                            Log.d("Task<PResult>", "Bad Extension Listener");
                        }
                        if (!isSynced())
                            iteratorExtension.remove();
                    }
                }
            }
        }
    }

    private void notifyFailureListener()
    {
        if (isExcepted()) {
            Iterator<OnFailureListener> iterator = onFailureListeners.descendingIterator();
            while (iterator.hasNext())
            {
                OnFailureListener listener = iterator.next();
                try {
                    listener.OnFailure(getException());
                } catch (Exception e) {
                    Log.d("Task<PResult>", "Bad Failure Listener");
                }
                if (!isSynced())
                    iterator.remove();
            }
        }
    }

    private void notifyCompleteListeners()
    {
        if (isComplete()) {

            Iterator<OnCompleteListener<PResult>> iterator = onCompleteListeners.descendingIterator();
            while (iterator.hasNext())
            {
                OnCompleteListener<PResult> listener = iterator.next();
                try {
                    listener.OnComplete(getResult());
                } catch (Exception e) {
                    Log.d("Task<PResult>", "Bad Complete Listener");
                }
                if (!isSynced())
                    iterator.remove();
            }
        }
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

    public void setExecutor(Executor executor) {
        synchronized (synchronizedObject) {
            this.executor = executor;
        }
    }

    public static final <PResult> Task<PResult> fromSource(TaskSource<PResult> taskSource)
    {
        return Tasks.execute(taskSource);
    }

    public boolean isSynced() {
        synchronized (synchronizedObject) {
            return keepSynced;
        }
    }

    public Task<PResult> keepSynced(boolean keepSynced) {
        synchronized (synchronizedObject) {
            this.keepSynced = keepSynced;
            return this;
        }
    }
}
