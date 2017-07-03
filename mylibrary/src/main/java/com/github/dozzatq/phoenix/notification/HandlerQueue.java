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
    private ArrayDeque<HandlerSupplier> handlerList;
    private final Object waitObject = new Object();

    HandlerQueue() {
        this.handlerList = new ArrayDeque<>();
    }

    @AnyThread
    void doHandler(@NonNull final String notificationKey, @NonNull final ArrayDeque<PhoenixNotification> notification)
    {
        ExceptionThrower.throwIfNotificationNull(notification);
        ExceptionThrower.throwIfQueueKeyNull(notificationKey);
        synchronized (waitObject)
        {
            Iterator<HandlerSupplier> iterator = handlerList.descendingIterator();
            while (iterator.hasNext())
            {
                final HandlerSupplier handler = iterator.next();
                handler.execute(new Runnable() {
                    @Override
                    public void run() {
                        handler.batchNotification(notificationKey, notification);
                    }
                });
            }
        }
    }

    @AnyThread
    void addHandler(@NonNull HandlerSupplier notificationHandler)
    {
        synchronized (waitObject)
        {
            handlerList.add(notificationHandler);
        }
    }

    @AnyThread
    void removeHandler(@NonNull NotificationHandler notificationHandler)
    {
        ExceptionThrower.throwIfHandlerNull(notificationHandler);
        synchronized (waitObject)
        {
            Iterator<HandlerSupplier> handlerSupplierIterator = handlerList.descendingIterator();
            while (handlerSupplierIterator.hasNext())
            {
                if (handlerSupplierIterator.next().equals(notificationHandler)){
                    handlerSupplierIterator.remove();
                    break;
                }
            }
        }
    }

}
