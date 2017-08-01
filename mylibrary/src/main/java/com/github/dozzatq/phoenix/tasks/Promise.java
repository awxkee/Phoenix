package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshyk on 30.07.2017.
 */

public class Promise extends TaskAlliance {

    public Promise(@NonNull TaskSource taskSource) {
        super(taskSource);
    }

    public Promise(@NonNull TaskSource... taskSources) {
        super(taskSources);
    }

    public Promise(Task... tasks) {
        super(tasks);
    }

    public Promise(Collection<? extends Task> taskCollection) {
        super(taskCollection);
    }

    public Promise(Task task) {
        super(task);
    }

    @NonNull
    public Promise extension(@NonNull final OnPromiseListener promiseListener)
    {
        return extension(MainThreadExecutor.getInstance(), promiseListener, false);
    }

    @NonNull
    public Promise extension(@NonNull Executor executor, @NonNull final OnPromiseListener promiseListener)
    {
        return extension(executor, promiseListener, false);
    }

    @NonNull
    public Promise extension(@NonNull Executor executor, @NonNull final OnPromiseListener promiseListener, final boolean keepSynced)
    {
        if (executor==null)
            throw new NullPointerException("Executor at Promise then must not be null!");
        if (promiseListener==null)
            throw new NullPointerException("OnPromiseListener at Promise then must not be null!");
        Task<Void> newTask = new Task<>();
        final Promise newPromise = new Promise(newTask);
        new PromiseListenerBridge(newPromise, promiseListener, keepSynced);
        return newPromise;
    }

}
