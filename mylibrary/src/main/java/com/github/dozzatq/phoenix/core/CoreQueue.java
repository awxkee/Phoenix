package com.github.dozzatq.phoenix.core;

import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;

import com.github.dozzatq.phoenix.notification.PhoenixNotification;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshyk on 04.06.2017.
 */

class CoreQueue {
    private ArrayDeque<NotificationHandler> handlerList;
    private Executor queueExecutor;
    private final Object waitObject = new Object();

    CoreQueue(Executor queueExecutor) {
        this.queueExecutor = queueExecutor;
        this.handlerList = new ArrayDeque<>();
    }

    @AnyThread
    public void doHandler(@NonNull final String notificationKey, @NonNull final PhoenixNotification notification)
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
                        handler.didNeedNotification(notificationKey, notification);
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
