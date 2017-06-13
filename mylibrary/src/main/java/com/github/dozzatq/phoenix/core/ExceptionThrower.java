package com.github.dozzatq.phoenix.core;

import com.github.dozzatq.phoenix.notification.OnActionComplete;
import com.github.dozzatq.phoenix.notification.PhoenixNotification;

import java.util.concurrent.Executor;

/**
 * Created by dxfb on 13.06.2017.
 */

class ExceptionThrower {
    static void throwIfNotificationNull(PhoenixNotification notification)
    {
        if (notification==null)
            throw new NullPointerException("Notification Listener must not be null.");
    }

    static void throwIfExecutorNull(Executor executor)
    {
        if (executor==null)
            throw new NullPointerException("Executor must not be null.");
    }

    static void throwIfHandlerNull(NotificationHandler handler)
    {
        if (handler==null)
            throw new NullPointerException("Handler must not be null.");
    }

    static void throwIfQueueKeyNull(String key)
    {
        if (key==null)
            throw new NullPointerException("Queue key must not be null.");
    }
}
