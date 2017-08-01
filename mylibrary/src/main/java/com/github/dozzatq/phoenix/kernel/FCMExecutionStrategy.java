package com.github.dozzatq.phoenix.kernel;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Rodion Bartoshyk on 01.08.2017.
 */

class FCMExecutionStrategy extends FCMTask {

    private ArrayDeque<FCMExecutor> fcmExecutors;
    private final Object mLock = new Object();

    FCMExecutionStrategy() {
        this.fcmExecutors = new ArrayDeque<>();
    }

    @Override
    public FCMTask add(@NonNull FCMExecutor fcmExecutor)
    {
        throwIfExecutorNull(fcmExecutor);
        synchronized (mLock) {
            fcmExecutors.add(fcmExecutor);
            return this;
        }
    }

    @Override
    public FCMTask remove(@NonNull FCMExecutor fcmExecutor) {
        throwIfExecutorNull(fcmExecutor);
        synchronized (mLock) {
            if (fcmExecutors.contains(fcmExecutor))
                fcmExecutors.remove(fcmExecutor);
            return this;
        }
    }

    @Override
    public FCMTask run(@NonNull Context context, @NonNull Map<String, String> dataMap)
    {
        throwIfContextNull(context);

        synchronized (mLock)
        {
            Iterator<FCMExecutor> fcmExecutorIterator = fcmExecutors.descendingIterator();
            FCMExecutor executor;
            while (fcmExecutorIterator.hasNext() ) {
                executor = fcmExecutorIterator.next();
                boolean callingResult = call(context, executor, dataMap);
            }
        }

        return this;
    }

    @Override
    public boolean call(@NonNull Context context,@NonNull FCMExecutor fcmExecutor, @NonNull Map<String, String> dataMap) {
        throwIfExecutorNull(fcmExecutor);
        throwIfContextNull(context);
        synchronized (mLock) {
            if (fcmExecutors.contains(fcmExecutor))
            {
                if (fcmExecutor.isSuccessIndex())
                {
                    fcmExecutor.setData(dataMap);
                    fcmExecutor.execute(context);
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public Deque<FCMExecutor> getDeque() {
        synchronized (mLock) {
            return fcmExecutors;
        }
    }

    @Override
    public int size() {
        synchronized (mLock) {
            return fcmExecutors.size();
        }
    }

    private void throwIfExecutorNull(@NonNull FCMExecutor fcmExecutor) {
        if (fcmExecutor == null)
            throw new NullPointerException("Push Executor must not be null !");
    }

    private void throwIfContextNull(@NonNull Context context) {
        if (context==null)
            throw new NullPointerException("Context must not be null !");
    }
}
