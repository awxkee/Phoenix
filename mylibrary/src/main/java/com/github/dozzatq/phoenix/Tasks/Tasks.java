package com.github.dozzatq.phoenix.Tasks;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by rodeon on 5/26/17.
 */

public class Tasks {
    private static ThreadPoolExecutor threadPoolExecutor;
    static {
        int numCores = Runtime.getRuntime().availableProcessors();
        threadPoolExecutor = new ThreadPoolExecutor(numCores * 2, numCores *2,
                60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }


    public static <PResult,ZResult> RuntimeTask<PResult,ZResult>
        executeRuntime(final RuntimeTaskSource<PResult,ZResult>  taskSource)
    {
        return executeRuntime(threadPoolExecutor, taskSource);
    }

    public static <PResult,ZResult> RuntimeTask<PResult,ZResult>
        executeRuntime(Executor executor, final RuntimeTaskSource<PResult,ZResult>  taskSource)
    {
        try {
            taskSource.setPoolExecutor((ThreadPoolExecutor) executor);
        }
        catch (ClassCastException e)
        {

        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    taskSource.setResult(taskSource.call());
                } catch (Exception e) {
                    taskSource.setException(e);
                }
            }
        });
        return taskSource.getTask();
    }

    public static <PResult> Task<PResult> execute(final TaskSource<PResult> taskSource)
    {
        return execute(threadPoolExecutor, taskSource);
    }

    public static <PResult> Task<PResult> execute(Executor executor, final TaskSource<PResult> taskSource)
    {
        taskSource.setExecutor(executor);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    taskSource.setResult(taskSource.call());
                } catch (Exception e) {
                    taskSource.setException(e);
                }
            }
        });
        return taskSource.getTask();
    }
}
