package com.github.dozzatq.phoenix.notification;

import android.support.annotation.NonNull;

import com.github.dozzatq.phoenix.activity.CallbackSupplier;
import com.github.dozzatq.phoenix.activity.StreetPolice;

import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshyk on 02.07.2017.
 */

class NotificationSupplier<T> extends CallbackSupplier<T> implements Executor{

    private boolean synced;
    private Executor executor;

    public NotificationSupplier(T object, StreetPolice streetPolice, boolean synced, Executor executor) {
        super(object, streetPolice);
        this.synced = synced;
        this.executor = executor;
    }

    public boolean isSynced() {
        synchronized (mLock) {
            return synced;
        }
    }

    @Override
    public void execute(@NonNull Runnable command) {
        synchronized (mLock) {
            executor.execute(command);
        }
    }
}
