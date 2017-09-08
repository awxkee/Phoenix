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

class FCMSchedulerStrategy extends FCMTask {

    private ArrayDeque<FCMScheduler> fcmSchedulers;
    private final Object mLock = new Object();

    FCMSchedulerStrategy() {
        this.fcmSchedulers = new ArrayDeque<>();
    }

    @Override
    public FCMTask add(@NonNull FCMScheduler fcmScheduler)
    {
        throwIfExecutorNull(fcmScheduler);
        synchronized (mLock) {
            fcmSchedulers.add(fcmScheduler);
            return this;
        }
    }

    @Override
    public FCMTask remove(@NonNull FCMScheduler fcmScheduler) {
        throwIfExecutorNull(fcmScheduler);
        synchronized (mLock) {
            if (fcmSchedulers.contains(fcmScheduler))
                fcmSchedulers.remove(fcmScheduler);
            return this;
        }
    }

    @Override
    public FCMTask run(@NonNull Context context, @NonNull Map<String, String> dataMap)
    {
        throwIfContextNull(context);

        synchronized (mLock)
        {
            Iterator<FCMScheduler> fcmExecutorIterator = fcmSchedulers.descendingIterator();
            FCMScheduler executor;
            while (fcmExecutorIterator.hasNext() ) {
                executor = fcmExecutorIterator.next();
                boolean callingResult = call(context, executor, dataMap);
            }
        }

        return this;
    }

    @Override
    public boolean call(@NonNull Context context, @NonNull FCMScheduler fcmScheduler, @NonNull Map<String, String> dataMap) {
        throwIfExecutorNull(fcmScheduler);
        throwIfContextNull(context);
        synchronized (mLock) {
            if (fcmSchedulers.contains(fcmScheduler))
            {
                if (fcmScheduler.isSuccessIndex())
                {
                    fcmScheduler.setData(dataMap);
                    fcmScheduler.execute(context);
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public Deque<FCMScheduler> getDeque() {
        synchronized (mLock) {
            return fcmSchedulers;
        }
    }

    @Override
    public int size() {
        synchronized (mLock) {
            return fcmSchedulers.size();
        }
    }

    private void throwIfExecutorNull(@NonNull FCMScheduler fcmScheduler) {
        if (fcmScheduler == null)
            throw new NullPointerException("Push Executor must not be null !");
    }

    private void throwIfContextNull(@NonNull Context context) {
        if (context==null)
            throw new NullPointerException("Context must not be null !");
    }
}
