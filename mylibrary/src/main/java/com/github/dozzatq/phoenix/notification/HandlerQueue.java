package com.github.dozzatq.phoenix.notification;

import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshyk on 04.06.2017.
 */

class HandlerQueue {
    private ArrayDeque<NotificationHandler> handlerList;
    private Executor queueExecutor;
    private final Object waitObject = new Object();

    HandlerQueue(Executor queueExecutor) {
        this.queueExecutor = queueExecutor;
        this.handlerList = new ArrayDeque<>();
    }

    @AnyThread
    public void doHandler(@NonNull final String notificationKey, @NonNull final ArrayDeque<PhoenixNotification> notification)
    {
        ExceptionThrower.throwIfNotificationNull(notification);
        ExceptionThrower.throwIfQueueKeyNull(notificationKey);
        synchronized (waitObject)
        {
            Iterator<NotificationHandler> iterator = handlerList.descendingIterator();
            while (iterator.hasNext())
            {
                final NotificationHandler handler = iterator.next();
                queueExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        handler.batchNotification(notificationKey, notification);
                    }
                });
            }
        }
    }

    @AnyThread
    public void addHandler(@NonNull NotificationHandler notificationHandler)
    {
        ExceptionThrower.throwIfHandlerNull(notificationHandler);
        synchronized (waitObject)
        {
            handlerList.add(notificationHandler);
        }
    }

    @AnyThread
    public void removeHandler(@NonNull NotificationHandler notificationHandler)
    {
        ExceptionThrower.throwIfHandlerNull(notificationHandler);
        synchronized (waitObject)
        {
            if (handlerList.contains(notificationHandler))
                handlerList.remove(notificationHandler);
        }
    }

}
