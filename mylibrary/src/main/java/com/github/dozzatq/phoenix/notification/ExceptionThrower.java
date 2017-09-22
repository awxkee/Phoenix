package com.github.dozzatq.phoenix.notification;

import com.github.dozzatq.phoenix.activity.CallbackSupplier;
import com.github.dozzatq.phoenix.activity.StreetPolice;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshik on 13.06.2017.
 */

class ExceptionThrower {
    static void throwIfNotificationNull(PhoenixNotification notification)
    {
        if (notification==null)
            throw new NullPointerException("Notification Listener must not be null!");
    }

    static void throwIfNotificationNull(ArrayDeque<PhoenixNotification> notification)
    {
        if (notification==null)
            throw new NullPointerException("Array Notification Listener must not be null!");
    }

    static void throwIfSupplierNull(CallbackSupplier notification)
    {
        if (notification==null)
            throw new NullPointerException("Notification Listener must not be null!");
    }

    static void throwIfHandlerNull(NotificationHandler handler)
    {
        if (handler==null)
            throw new NullPointerException("Handler must not be null.");
    }

    static void throwIfExecutorNull(Executor executor)
    {
        if (executor==null)
            throw new NullPointerException("Executor must not be null!");
    }

    static void throwIfActionNull(OnActionComplete onActionComplete)
    {
        if (onActionComplete==null)
            throw new NullPointerException("Action must not be null!");
    }

    static void throwIfQueueKeyNull(String key)
    {
        if (key==null)
            throw new NullPointerException("Queue key must not be null.");
    }

    static void throwIfStreetPolicyNull(StreetPolice streetPolice)
    {
        if (streetPolice==null)
            throw new IllegalArgumentException("StreetPolice must not be null !");
    }
}
