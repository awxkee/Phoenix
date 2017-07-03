package com.github.dozzatq.phoenix.tasks;

import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    public static Executor getDefaultExecutor()
    {
        return threadPoolExecutor;
    }

    public static <PResult> PResult await(@NonNull Task<PResult> task) throws ExecutionException,InterruptedException{
        if (Looper.getMainLooper()==Looper.myLooper())
            throw new IllegalStateException("Tasks.await must not be called on the main thread");
        if(task == null) {
            throw new NullPointerException("Task must not be null");
        }
        if (task.isComplete())
            return getResultTask(task);
        else {
            WaiterTask waiterTask = new WaiterTask();
            task.addOnSuccessListener(MainThreadExecutor.CURRENT_THREAD_EXECUTOR, waiterTask);
            task.addOnFailureListener(MainThreadExecutor.CURRENT_THREAD_EXECUTOR, waiterTask);
            waiterTask.await();
            return getResultTask(task);
        }
    }

    private static <TResult> TResult getResultTask(Task<TResult> task) throws ExecutionException {
        if(task.isSuccessful()) {
            return task.getResult();
        } else {
            throw new ExecutionException(task.getException());
        }
    }

    public static TaskAlliance allianceTask(Task... tasks)
    {
        return new TaskAlliance(tasks);
    }

    public static TaskAlliance whenAll(Task... tasks)
    {
        return allianceTask(tasks);
    }

    public static TaskAlliance whenAll(Collection<Task> taskCollection)
    {
        return allianceTask(taskCollection);
    }

    public static TaskUnion whenSame(Task... tasks)
    {
        return new TaskUnion(tasks);
    }

    public static TaskUnion whenSame(Collection<Task> taskCollection)
    {
        return new TaskUnion(taskCollection);
    }

    public static TaskAlliance allianceTask(Collection<Task> taskCollection)
    {
        return new TaskAlliance(taskCollection);
    }

    public static TaskAlliance allianceTask(Task task)
    {
        return new TaskAlliance(task);
    }

    public static <PResult> Task<PResult> execute(final TaskSource<PResult> taskSource)
    {
        return execute(getDefaultExecutor(), taskSource);
    }

    public static <PResult> ControllableTask<PResult> execute(final ControllableSource<PResult> controllableSource)
    {
        return execute(getDefaultExecutor(), controllableSource);
    }

    public static <PResult> ControllableTask<PResult> execute(Executor executor, final ControllableSource<PResult> taskSource)
    {
        executor.execute(taskSource.getRunnable());
        return taskSource.getTask();
    }

    public static <PResult> CancellableTask<PResult> execute(final CancellableSource<PResult> controllableSource)
    {
        return execute(getDefaultExecutor(), controllableSource);
    }

    public static <PResult> CancellableTask<PResult> execute(Executor executor, final CancellableSource<PResult> taskSource)
    {
        executor.execute(taskSource.getRunnable());
        return taskSource.getTask();
    }

    public static <PResult> Task<PResult> execute(Executor executor, final TaskCompletionSource<PResult> taskSource)
    {
        executor.execute(taskSource.getRunnable());
        return taskSource.getTask();
    }

    private static class WaiterTask implements MainListenerService {
        private final CountDownLatch countDownLatch;

        private WaiterTask() {
            this.countDownLatch = new CountDownLatch(1);
        }

        public void await() throws InterruptedException {
            this.countDownLatch.await();
        }

        public boolean await(long var1, TimeUnit var3) throws InterruptedException, TimeoutException {
            return this.countDownLatch.await(var1, var3);
        }

        @Override
        public void OnFailure(@NonNull Exception e) {
            this.countDownLatch.countDown();
        }

        @Override
        public void OnSuccess(Object o) {
            this.countDownLatch.countDown();
        }
    }

    interface MainListenerService extends OnFailureListener, OnSuccessListener {
    }
}
