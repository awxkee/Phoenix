package com.github.dozzatq.phoenix.Core56;

import com.github.dozzatq.phoenix.Notification56.PhoenixNotification;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.Executor;

/**
 * Created by dxfb on 04.06.2017.
 */

class CoreQueue {
    private ArrayDeque<NotificationHandler> handlerList;
    private Executor queueExecutor;
    private final Object waitObject = new Object();

    CoreQueue(Executor queueExecutor) {
        this.queueExecutor = queueExecutor;
        this.handlerList = new ArrayDeque<>();
    }

    public void doQueueSingle(final String notificationKey, final PhoenixNotification notification)
    {
        synchronized (waitObject)
        {
            if (queueExecutor==null || handlerList==null) {
                throw  new NullPointerException("Executor and Queue must not be null!");
            }
            Iterator<NotificationHandler> iterator = handlerList.descendingIterator();
            while (iterator.hasNext())
            {
                final NotificationHandler handler = iterator.next();
                queueExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        handler.didNeedNotificationSingle(notificationKey, notification);
                    }
                });
            }
        }
    }

    public void doQueue(final String notificationKey, final PhoenixNotification notification)
    {
        synchronized (waitObject)
        {
            if (queueExecutor==null) {
                throw  new NullPointerException("Executor and Queue must not be null!");
            }
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

    public void addHandler(NotificationHandler notificationHandler)
    {
        throwIfHandlerNull(notificationHandler);
        synchronized (waitObject)
        {
            handlerList.add(notificationHandler);
        }
    }

    public void removeHandler(NotificationHandler notificationHandler)
    {
        throwIfHandlerNull(notificationHandler);
        synchronized (waitObject)
        {
            if (handlerList.contains(notificationHandler))
                handlerList.remove(notificationHandler);
        }
    }

    private void throwIfHandlerNull(NotificationHandler notificationHandler)
    {
        if (notificationHandler==null)
            throw new NullPointerException("NotificationHandler must not be null!");
    }
}
