package com.github.dozzatq.phoenix.Tasks;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by rodeon on 5/26/17.
 */

public abstract class RuntimeTaskSource<PResult, ZResult> extends TaskSource<PResult> {
    protected RuntimeTask<PResult,ZResult> task = new RuntimeTask<>();
    private ThreadPoolExecutor poolExecutor;

    @Override
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

    @Override
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

    public boolean setPublish(ZResult zResult)
    {
        try{
            task.setPublish(zResult);
        }
        catch (Exception e)
        {
            task.setException(e);
            return false;
        }
        return true;
    }

    @Override
    public RuntimeTask<PResult,ZResult> getTask()
    {
        return task;
    }

    public void setPoolExecutor(ThreadPoolExecutor poolExecutor) {
        this.poolExecutor = poolExecutor;
        task.setExecutor(poolExecutor);
    }
}
