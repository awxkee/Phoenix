package com.github.dozzatq.phoenix.tasks;

import android.os.Looper;
import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Rodion Bartoshik on 5/26/17.
 */

public class Tasks {
    private static final ThreadPoolExecutor threadPoolExecutor;

    static {
        int numCores = Runtime.getRuntime().availableProcessors();
        threadPoolExecutor = new ThreadPoolExecutor(numCores * 2, numCores *2,
                60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    @NonNull
    @AnyThread
    public static Executor getDefaultExecutor()
    {
        return threadPoolExecutor;
    }

    @WorkerThread
    public static <PResult> PResult await(@NonNull Task<PResult> task) throws ExecutionException,InterruptedException{
        if (Looper.getMainLooper()==Looper.myLooper())
            throw new IllegalStateException("Tasks.await must not be called on the main thread");
        throwIfTaskNull(task);
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

    @AnyThread
    private static <TResult> TResult getResultTask(@NonNull Task<TResult> task) throws ExecutionException {
        throwIfTaskNull(task);
        if(task.isSuccessful()) {
            return task.getResult();
        } else {
            throw new ExecutionException(task.getException());
        }
    }

    private static <TResult> void throwIfTaskNull(@NonNull Task<TResult> task) {
        if (task==null)
            throw new NullPointerException("Task must not be null!");
    }

    @AnyThread
    public static TaskAlliance allianceTask(@NonNull Task... tasks)
    {
        return new TaskAlliance(tasks);
    }

    @AnyThread
    public static TaskAlliance whenAll(@NonNull Task... tasks)
    {
        return allianceTask(tasks);
    }

    @AnyThread
    public static TaskAlliance whenAll(@NonNull Collection<? extends Task> taskCollection)
    {
        return allianceTask(taskCollection);
    }

    @AnyThread
    public static TaskUnion whenSame(@NonNull Task... tasks)
    {
        return new TaskUnion(tasks);
    }

    @AnyThread
    public static TaskUnion whenSame(@NonNull Collection<? extends Task> taskCollection)
    {
        return new TaskUnion(taskCollection);
    }

    @AnyThread
    public static TaskAlliance allianceTask(@NonNull Collection<? extends Task> taskCollection)
    {
        return new TaskAlliance(taskCollection);
    }

    @AnyThread
    public static TaskAlliance allianceTask(@NonNull Task task)
    {
        return new TaskAlliance(task);
    }

    @AnyThread
    public static <PResult> Task<PResult> execute(@NonNull final TaskSource<PResult> taskSource)
    {
        throwIfTaskSourceNull(taskSource);
        return execute(getDefaultExecutor(), taskSource);
    }

    private static void throwIfTaskSourceNull(TaskCompletionSource taskSource)
    {
        if (taskSource==null)
            throw new NullPointerException("Task Source must not be null!");
    }

    @AnyThread
    public static <PResult> ControllableTask<PResult> execute(@NonNull final ControllableSource<PResult> controllableSource)
    {
        throwIfTaskSourceNull(controllableSource);
        return execute(getDefaultExecutor(), controllableSource);
    }

    @AnyThread
    public static <PResult> ControllableTask<PResult> execute(@NonNull Executor executor,@NonNull final ControllableSource<PResult> taskSource)
    {
        throwIfExecutorNull(executor);
        throwIfTaskSourceNull(taskSource);
        executor.execute(taskSource.getRunnable());
        return taskSource.getTask();
    }

    @AnyThread
    public static Task<Void> execute(@NonNull final Runnable runnable)
    {
        return execute(getDefaultExecutor(), runnable);
    }

    @AnyThread
    public static Task<Void> execute(@NonNull Executor executor, @NonNull final Runnable runnable)
    {
        throwIfExecutorNull(executor);
        throwIfRunnableNull(runnable);
        final Task<Void> task = new Task<>();
        threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        runnable.run();
                        task.setResult(null);
                    }
                    catch (Exception exception)
                    {
                        task.setException(exception);
                    }
                }
        });
        return task;
    }

    private static void throwIfRunnableNull(@NonNull Runnable runnable) {
        if (runnable==null)
            throw new NullPointerException("Runnable must not be null");
    }

    @AnyThread
    public static <PResult> CancellableTask<PResult> execute(@NonNull final CancellableSource<PResult> controllableSource)
    {
        throwIfTaskSourceNull(controllableSource);
        return execute(getDefaultExecutor(), controllableSource);
    }

    @AnyThread
    public static <PResult> CancellableTask<PResult> execute(@NonNull Executor executor,@NonNull final CancellableSource<PResult> taskSource)
    {
        throwIfExecutorNull(executor);
        throwIfTaskSourceNull(taskSource);
        executor.execute(taskSource.getRunnable());
        return taskSource.getTask();
    }

    private static void throwIfExecutorNull(Executor executor)
    {
        if (executor==null)
            throw new NullPointerException("Executor must not be null");
    }

    @AnyThread
    public static <PResult> Task<PResult> execute(@NonNull Executor executor,@NonNull final TaskCompletionSource<PResult> taskSource)
    {
        throwIfExecutorNull(executor);
        throwIfTaskSourceNull(taskSource);
        executor.execute(taskSource.getRunnable());
        return taskSource.getTask();
    }

    private static class WaiterTask implements MainListenerService {
        private final CountDownLatch countDownLatch;

        private WaiterTask() {
            this.countDownLatch = new CountDownLatch(1);
        }

        void await() throws InterruptedException {
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
