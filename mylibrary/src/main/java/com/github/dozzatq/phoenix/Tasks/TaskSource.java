package com.github.dozzatq.phoenix.Tasks;

/**
 * Created by rodeon on 5/26/17.
 */

public abstract class TaskSource<PResult> {
    protected Task<PResult> task = new Task<PResult>();
    public abstract PResult call() throws Exception;

    public void setResult(PResult pResult)
    {
        try{
            task.setResult(pResult);
        }
        catch (Exception e)
        {
            task.setException(e);
        }
    }

    public void setException(Exception exception)
    {
        try{
            task.setException(exception);
        }
        catch (Exception e)
        {
            task.setException(e);
        }
    }

    public Task<PResult> getTask() {
        return task;
    }

}
