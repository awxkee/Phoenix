package com.github.dozzatq.phoenix.Util;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rodeon on 4/3/17.
 */
public abstract class CrossTask<R,T> implements Task.OnCompleteListener<T>, Task.OnFailureListener{

    private final static String TAG = "CrossTask<R,T>";

    private Task<T> crossTasksList;
    private Task<R> rCrossTask;

    private List<OnCrossCompleteListener<R,T>> onCrossCompleteResult = new ArrayList<>();

    public CrossTask<R,T> crossWithTask(Task<R> rToCross, Task<T> toCross)
    {
        crossTasksList=toCross;
        rCrossTask = rToCross;

        toCross.addOnCompleteListener(this);
        rCrossTask.addOnFailureListener(this);
        rCrossTask.addOnCompleteListener(new Task.OnCompleteListener<R>() {
            @Override
            public void OnCompleteListener(R result) {
                setsCrossedTaskResult(result);
                if (getCrossedTaskResult()!=null)
                {
                    notifyCrossedCompleteListeners();
                }
            }
        });

        return this;
    }

    @NonNull
    public CrossTask<R,T> addOnCrossCompleteListener(@NonNull OnCrossCompleteListener<R,T> listener)
    {
        onCrossCompleteResult.add(listener);
        if (hassCrossedTaskResult() && hasCrossedTaskResult())
        {
            try{
                listener.OnCrossCompleteListener(getsCrossedTaskResult(), getCrossedTaskResult());}
            catch (Exception e){
                Log.d(TAG, "Bad Complete Listener");
            }
        }
        return this;
    }

    @Override
    public void OnCompleteListener(T result)
    {
        setCrossedTaskResult(result);
        if (getsCrossedTaskResult()!=null)
        {
            notifyCrossedCompleteListeners();
        }
    }

    @Override
    public void OnFailureListener(Exception exception)
    {
        cancelTask();
    }

    public void notifyCrossedCompleteListeners()
    {
        if (hassCrossedTaskResult() && hasCrossedTaskResult()) {
            for (OnCrossCompleteListener<R, T> listener : onCrossCompleteResult) {
                try {
                    listener.OnCrossCompleteListener(getsCrossedTaskResult(), getCrossedTaskResult());
                } catch (Exception e) {
                    Log.d(TAG, "Bad Complete Listener");
                }
            }
        }
    }

    private T crossedTaskResult;
    private R sCrossedTaskResult;

    public abstract void cancelTask();

    public boolean hasCrossedTaskResult()
    {
        return getCrossedTaskResult() != null;
    }

    public boolean hassCrossedTaskResult()
    {
        return getsCrossedTaskResult() != null;
    }

    private T getCrossedTaskResult() {
        return crossedTaskResult;
    }

    private void setCrossedTaskResult(T crossedTaskResult) {
        this.crossedTaskResult = crossedTaskResult;
    }

    public R getsCrossedTaskResult() {
        return sCrossedTaskResult;
    }

    public void setsCrossedTaskResult(R sCrossedTaskResult) {
        this.sCrossedTaskResult = sCrossedTaskResult;
    }

    public interface OnCrossCompleteListener<R,T>
    {
        public void OnCrossCompleteListener(R result1, T result);
    }
}
