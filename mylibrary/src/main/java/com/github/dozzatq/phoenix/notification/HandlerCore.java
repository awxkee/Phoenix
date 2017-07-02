package com.github.dozzatq.phoenix.notification;

import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;

import com.github.dozzatq.phoenix.tasks.MainThreadExecutor;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshyk on 08.12.2016.
 */

public class HandlerCore {

    private static HandlerCore ourInstance = null;

    private final Object mLock = new Object();

    @AnyThread
    public static HandlerCore getInstance() {
        HandlerCore localInstance = ourInstance;
        if (localInstance == null) {
            synchronized (HandlerCore.class) {
                localInstance = ourInstance;
                if (localInstance == null) {
                    ourInstance = localInstance = new HandlerCore();
                }
            }
        }
        return localInstance;
    }

    private Map<String, HandlerQueue> handlerList;

    private HandlerCore() {
        handlerList = new HashMap<>();
    }

    @AnyThread
    public HandlerCore addNotificationHandler(@NonNull String notificationKey,
                                              @NonNull NotificationHandler handler)

    {
        return addNotificationHandler(MainThreadExecutor.getInstance(), notificationKey, handler);
    }

    @AnyThread
    public HandlerCore addNotificationHandler(@NonNull Executor executor,
                                              @NonNull String notificationKey,
                                              @NonNull NotificationHandler handler)
    {
        ExceptionThrower.throwIfExecutorNull(executor);
        ExceptionThrower.throwIfHandlerNull(handler);
        ExceptionThrower.throwIfQueueKeyNull(notificationKey);
        synchronized (mLock) {
            HandlerQueue notificationHandlerList = null;
            if (handlerList.containsKey(notificationKey))
                notificationHandlerList = handlerList.get(notificationKey);
            else
                notificationHandlerList = new HandlerQueue(executor);
            notificationHandlerList.addHandler(handler);
            handlerList.put(notificationKey, notificationHandlerList);
            return this;
        }
    }

    @AnyThread
    public boolean hasHandler(@NonNull String notificationKey)
    {
        ExceptionThrower.throwIfQueueKeyNull(notificationKey);
        synchronized (mLock)
        {
            return handlerList.containsKey(notificationKey) && handlerList.get(notificationKey)!=null;
        }
    }

    @AnyThread
    public HandlerCore removeNotificationHandler(@NonNull String notificationKey, @NonNull NotificationHandler handler)
    {
        ExceptionThrower.throwIfHandlerNull(handler);
        ExceptionThrower.throwIfQueueKeyNull(notificationKey);
        synchronized (mLock) {
            HandlerQueue notificationHandlerList = null;
            if (handlerList.containsKey(notificationKey))
                notificationHandlerList = handlerList.get(notificationKey);
            if (notificationHandlerList == null)
                return this;
            notificationHandlerList.removeHandler(handler);
            return this;
        }
    }

    @AnyThread
    void beginBatchedUpdate(@NonNull String notificationKey, ArrayDeque<PhoenixNotification> phoenixNotifications)
    {
        synchronized (mLock) {
            ExceptionThrower.throwIfNotificationNull(phoenixNotifications);
            ExceptionThrower.throwIfQueueKeyNull(notificationKey);
            synchronized (mLock) {
                HandlerQueue notificationHandlerList = null;
                if (handlerList.containsKey(notificationKey))
                    notificationHandlerList = handlerList.get(notificationKey);
                if (notificationHandlerList == null)
                    return;
                notificationHandlerList.doHandler(notificationKey, phoenixNotifications);
            }
        }
    }

    @AnyThread
    void initiateListener(@NonNull String notificationKey, @NonNull PhoenixNotification phoenixNotification)
    {
        ExceptionThrower.throwIfNotificationNull(phoenixNotification);
        ExceptionThrower.throwIfQueueKeyNull(notificationKey);
        synchronized (mLock) {
            HandlerQueue notificationHandlerList = null;
            if (handlerList.containsKey(notificationKey))
                notificationHandlerList = handlerList.get(notificationKey);
            if (notificationHandlerList == null)
                return;
            ArrayDeque<PhoenixNotification> phoenixNotifications = new ArrayDeque<PhoenixNotification>();
            phoenixNotifications.push(phoenixNotification);
            notificationHandlerList.doHandler(notificationKey, phoenixNotifications);
        }
    }
}
