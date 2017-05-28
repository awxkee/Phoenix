package com.github.dozzatq.phoenix.Tasks;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class Task<PResult> {
    private PResult taskResult;
    private Exception exception;
    private List<OnSuccessListener<PResult>> onSuccessListeners;
    private List<OnFailureListener> onFailureListeners;
    private List<OnCompleteListener<PResult>> onCompleteListeners;
    private List<OnExtensionListener<PResult>> onExtensionListeners;
    private Object taskTag;
    private boolean isComplete;
    private boolean isExcepted;
    private Executor executor;

    public Task() {
        onSuccessListeners = Collections.synchronizedList(new ArrayList<OnSuccessListener<PResult>>());
        onFailureListeners = Collections.synchronizedList(new ArrayList<OnFailureListener>());
        onCompleteListeners = Collections.synchronizedList(new ArrayList<OnCompleteListener<PResult>>());
        onExtensionListeners = Collections.synchronizedList(new ArrayList<OnExtensionListener<PResult>>());
        isComplete = false;
        isExcepted = false;
    }

    public boolean isComplete()
    {
        return isComplete;
    }

    public boolean isSuccessful()
    {
        return isComplete && !isExcepted;
    }

    public boolean isExcepted()
    {
        return isExcepted;
    }

    public void setResult(PResult pResult)
    {
        isComplete = true;
        taskResult = pResult;
        notifyCompleteListeners();
        notifySuccessListeners();
    }

    public Exception getException()
    {
        return exception;
    }

    public PResult getResult()
    {
        if (isComplete())
            return taskResult;
        else return null;
    }

    public void setException(Exception exception1)
    {
        isExcepted = true;
        exception = exception1;
        notifyFailureListener();
    }

    @NonNull
    public Task<PResult> addOnSuccessListener(@NonNull OnSuccessListener<PResult> listener)
    {
        onSuccessListeners.add(listener);
        if (getResult()!=null)
        {
            try{
                listener.OnSuccess(getResult());
            }
            catch (Exception e)
            {
                Log.d("Task<PResult>", "Bad Success Listener");
            }
        }
        return this;
    }

    @NonNull
    private Task<PResult> addOnExtensionListener(@NonNull OnExtensionListener<PResult> listener)
    {
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

    @NonNull
    public Task<PResult> addOnCompleteListener(@NonNull OnCompleteListener<PResult> listener)
    {
        onCompleteListeners.add(listener);
        if (isComplete())
        {
            try{
                listener.OnComplete(getResult());}
            catch (Exception e){
                Log.d("Task<PResult>", "Bad Complete Listener");
            }
        }
        return this;
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
        onFailureListeners.add(listener);
        if (isExcepted())
        {
            try {
                notifyFailureListener();
            }
            catch (Exception e)
            {
                Log.d("Task<PResult>", "Bad Failure Listener");
            }
        }
        return this;
    }

    private void notifySuccessListeners()
    {
        if (isSuccessful())
        {
        for (OnSuccessListener<PResult> listener : onSuccessListeners) {
            try {
                listener.OnSuccess(getResult());
            }
            catch (Exception e)
            {
                Log.d("Task<PResult>", "Bad Success Listener");
            }
        }
        }
    }

    private void notifyFailureListener()
    {
        if (isExcepted()) {
            for (OnFailureListener listener : onFailureListeners) {
                try {
                    listener.OnFailure(getException());
                } catch (Exception e1) {
                    Log.d("Task<PResult>", "Bad Failure Listener");
                }
            }
        }
    }

    private void notifyCompleteListeners()
    {
        if (isComplete()) {
            for (OnCompleteListener<PResult> listener : onCompleteListeners) {
                try {
                    listener.OnComplete(getResult());
                } catch (Exception e) {
                    Log.d("Task<PResult>", "Bad Complete Listener");
                }
            }
            for (OnExtensionListener<PResult> listener : onExtensionListeners) {
                try {
                    listener.OnExtension(this);
                } catch (Exception e) {
                    Log.d("Task<PResult>", "Bad Extension Listener");
                }
            }
        }
    }

    public Object getTaskTag() {
        return taskTag;
    }

    public void setTaskTag(Object taskTag) {
        this.taskTag = taskTag;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }


}
