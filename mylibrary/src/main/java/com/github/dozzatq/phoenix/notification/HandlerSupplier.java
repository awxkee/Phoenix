package com.github.dozzatq.phoenix.notification;

import android.support.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;

/**
 * Created by dxfb on 03.07.2017.
 */

class HandlerSupplier implements Executor {
    private NotificationHandler notificationHandler;
    private Executor executor;

    HandlerSupplier(NotificationHandler notificationHandler, Executor executor) {
        this.notificationHandler = notificationHandler;
        this.executor = executor;
    }

    public boolean equals(NotificationHandler notificationHandler)
    {
        return this.notificationHandler.equals(notificationHandler);
    }

    void batchNotification(@NonNull String key, @NonNull ArrayDeque<PhoenixNotification> phoenixNotifications)
    {
        notificationHandler.batchNotification(key, phoenixNotifications);
    }

    @Override
    public void execute(@NonNull Runnable command) {
        executor.execute(command);
    }

}
