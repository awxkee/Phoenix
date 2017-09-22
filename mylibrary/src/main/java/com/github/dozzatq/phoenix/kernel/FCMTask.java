package com.github.dozzatq.phoenix.kernel;

import android.content.Context;
import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import java.util.Deque;
import java.util.Map;

/**
 * Created by Rodion Bartoshik on 01.08.2017.
 */

public abstract class FCMTask {

    @AnyThread
    public abstract FCMTask add(@NonNull FCMScheduler fcmScheduler);
    @AnyThread
    public abstract FCMTask remove(@NonNull FCMScheduler fcmScheduler);
    @WorkerThread
    public abstract FCMTask run(@NonNull Context context, @NonNull Map<String, String> dataMap);
    @WorkerThread
    public abstract boolean call(@NonNull Context context,
                                 @NonNull FCMScheduler fcmScheduler,
                                 @NonNull Map<String, String> dataMap);
    public abstract Deque<FCMScheduler> getDeque();
    public abstract int size();

    @NonNull
    public static FCMTask get()
    {
        return new FCMSchedulerStrategy();
}
}
