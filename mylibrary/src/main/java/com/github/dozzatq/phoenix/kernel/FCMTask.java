package com.github.dozzatq.phoenix.kernel;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Deque;
import java.util.Map;

/**
 * Created by Rodion Bartoshyk on 01.08.2017.
 */

public abstract class FCMTask {

    public abstract FCMTask add(@NonNull FCMExecutor fcmExecutor);
    public abstract FCMTask remove(@NonNull FCMExecutor fcmExecutor);
    public abstract FCMTask run(@NonNull Context context, @NonNull Map<String, String> dataMap);
    public abstract boolean call(@NonNull Context context, @NonNull FCMExecutor fcmExecutor, @NonNull Map<String, String> dataMap);
    public abstract Deque<FCMExecutor> getDeque();
    public abstract int size();

    @NonNull
    public static FCMTask get()
    {
        return new FCMExecutionStrategy();
}
}
