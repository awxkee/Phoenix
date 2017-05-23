package com.github.dozzatq.phoenix.Util;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rodion on 24.11.16.
 */

public abstract class Task<R> {
    public Object getTaskTag() {
        return taskTag;
    }

    public void setTaskTag(Object taskTag) {
        this.taskTag = taskTag;
    }

    public boolean hasCompleteResult()
    {
        return getCompleteResult() != null;
    }

    public boolean hasSuccessResult()
    {
        return getSuccessResult() != null;
    }

    public boolean hasException()
    {
        return getException() != null;
    }

    public R getCompleteResult() {
        return completeResult;
    }

    public void setCompleteResult(R completeResult) {
        this.completeResult = completeResult;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public R getSuccessResult() {
        return successResult;
    }

    public void setSuccessResult(R successResult) {
        this.successResult = successResult;
    }

    public interface OnSuccessListener<R>
    {
        public void OnSuccessListener(R result);
    }
    public interface OnFailureListener
    {
        public void OnFailureListener(Exception exception);
    }
    public interface OnCanceledListener{
        public void OnCanceledListener();
    }
    public interface OnCompleteListener<R>
    {
        public void OnCompleteListener(R result);
    }
    private R completeResult;
    private R successResult;
    private Exception exception;
    private List<OnSuccessListener<R>> onSuccessListeners = new ArrayList<>();
    private List<OnFailureListener> onFailureListeners = new ArrayList<>();
    private List<OnCompleteListener<R>> onCompleteListeners = new ArrayList<>();
    private List<OnCanceledListener> onCanceledListeners = new ArrayList<>();

    private Object taskTag;

    @NonNull
    public Task<R> addOnCanceledListener(OnCanceledListener canceledListener)
    {
        onCanceledListeners.add(canceledListener);
        return this;
    }

    @NonNull
    public Task<R> addOnSuccessListener(@NonNull OnSuccessListener<R> listener)
    {
        onSuccessListeners.add(listener);
        if (hasSuccessResult())
        {
            try{
            listener.OnSuccessListener(getSuccessResult());
            }
            catch (Exception e)
            {
                Log.d("Task<R>", "Bad Success Listener");
            }
        }
        return this;
    }

    @NonNull
    public Task<R> addOnCompleteListener(@NonNull OnCompleteListener<R> listener)
    {
        onCompleteListeners.add(listener);
        if (hasCompleteResult())
        {
            try{
            listener.OnCompleteListener(getCompleteResult());}
            catch (Exception e){
                Log.d("Task<R>", "Bad Complete Listener");
            }
        }
        return this;
    }

    @NonNull
    public Task<R> addOnFailureListener(@NonNull OnFailureListener listener)
    {
        onFailureListeners.add(listener);
        if (hasException())
        {
            try {
                notifyFailureListener(getException());
            }
            catch (Exception e)
            {
                Log.d("Task<R>", "Bad Failure Listener");
            }
        }
        return this;
    }

    public void notifyCanceledListeners()
    {
        for (OnCanceledListener listener : onCanceledListeners) {
            listener.OnCanceledListener();
        }
    }

    public void notifySuccessListeners(R template)
    {
        setSuccessResult(template);
        for (OnSuccessListener<R> listener : onSuccessListeners) {
            try {
                listener.OnSuccessListener(template);
            }
            catch (Exception e)
            {
                Log.d("Task<R>", "Bad Success Listener");
            }
        }
    }

    public void notifyFailureListener(Exception e)
    {
        setException(e);
        for (OnFailureListener listener : onFailureListeners) {
            try{
            listener.OnFailureListener(e);
            }
            catch (Exception e1){
                Log.d("Task<R>", "Bad Failure Listener");
            }
        }
    }

    public void notifyCompleteListeners(R template)
    {
        setCompleteResult(template);
        for (OnCompleteListener<R> listener : onCompleteListeners) {
            try {
                listener.OnCompleteListener(template);
            }
            catch (Exception e)
            {
                Log.d("Task<R>", "Bad Complete Listener");
            }
        }
    }

    public abstract void cancelTask();
}
