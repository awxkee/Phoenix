package com.github.dozzatq.phoenix.tasks;

import java.util.concurrent.Callable;

/**
 * Created by Rodion Bartoshyk on 10.06.2017.
 */

public abstract class TaskCompletionSource<PTask> implements Callable<PTask> {
    public abstract Task<PTask> getTask();

    public final void setResult(PTask pResult)
    {
        try{
            getTask().setResult(pResult);
        }
        catch (Exception e)
        {
            getTask().setException(e);
        }
    }

    public final void setException(Exception exception)
    {
        try{
            getTask().setException(exception);
        }
        catch (Exception e)
        {
            getTask().setException(e);
        }
    }

    private final Runnable taskRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                setResult(call());
            } catch (Exception e) {
                setException(e);
            }
        }
    };

    public final Runnable getRunnable()
    {
        return taskRunnable;
    }

    public String getTag()
    {
        return "TaskCompletionSource";
    }

    @Override
    public String toString()
    {
        if (getTask().isSuccessful()) {
            if (getTask().getResult() != null)
                return new StringBuilder(getTag()).append(" state successful with result : ").append(getTask().getResult().toString()).toString();
            else
                return throwConvertException();
        }
        else if (getTask().isComplete()) {
            if (getTask().getResult() != null)
                return new StringBuilder(getTag()).append(" state completed with result : ").append(getTask().getResult().toString()).toString();
            else
                return throwConvertException();
        }
        else if (getTask().isExcepted()) {
            if (getTask().getException() != null)
                return new StringBuilder(getTag()).append(" state excepted with result : ").append(getTask().getException().toString()).toString();
            else return throwConvertException();
        }
        else
            return new StringBuilder(getTag()).append(" state waiting for events !").toString();
    }

    private final String throwConvertException()
    {
        return new StringBuilder(getTag()).append(" toString IllegalStateException !").toString();
    }
}
