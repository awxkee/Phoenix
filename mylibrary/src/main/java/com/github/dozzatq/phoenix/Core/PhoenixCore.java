package com.github.dozzatq.phoenix.Core;

import android.content.Context;
import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.dozzatq.phoenix.Phoenix;
import com.github.dozzatq.phoenix.Notification.PhoenixNotification;
import com.github.dozzatq.phoenix.Tasks.DefaultExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by dxfb on 08.12.2016.
 */

public class PhoenixCore {

    private static PhoenixCore ourInstance = null;

    private final Object waitObject = new Object();

    @AnyThread
    public static PhoenixCore getInstance() {
        PhoenixCore localInstance = ourInstance;
        if (localInstance == null) {
            synchronized (PhoenixCore.class) {
                localInstance = ourInstance;
                if (localInstance == null) {
                    ourInstance = localInstance = new PhoenixCore();
                }
            }
        }
        return localInstance;
    }

    private Map<String, CoreQueue> handlerList;

    private PhoenixCore() {
        if (Phoenix.getInstance().getContext() == null)
            throw new IllegalStateException("Phoenix must be inited !");
        handlerList = new HashMap<>();
    }

    public PhoenixCore addNotificationHandler(@NonNull String notificationKey,
                                              @NonNull NotificationHandler handler)

    {
        return addNotificationHandler(DefaultExecutor.getInstance(), notificationKey, handler);
    }

    public PhoenixCore addNotificationHandler(@NonNull Executor executor,
                                              @NonNull String notificationKey,
                                              @NonNull NotificationHandler handler)
    {
        synchronized (waitObject) {
            if (notificationKey == null)
                throw new NullPointerException("Notification key must not be null");
            CoreQueue notificationHandlerList = null;
            if (handlerList.containsKey(notificationKey))
                notificationHandlerList = handlerList.get(notificationKey);
            else
                notificationHandlerList = new CoreQueue(executor);
            notificationHandlerList.addHandler(handler);
            handlerList.put(notificationKey, notificationHandlerList);
            return this;
        }
    }

    public PhoenixCore removeNotificationHandler(String notificationKey, NotificationHandler handler)
    {
        synchronized (waitObject) {
            if (notificationKey == null)
                throw new NullPointerException("Notification key must not be null");
            CoreQueue notificationHandlerList = null;
            if (handlerList.containsKey(notificationKey))
                notificationHandlerList = handlerList.get(notificationKey);
            if (notificationHandlerList == null)
                return this;
            notificationHandlerList.removeHandler(handler);
            return this;
        }
    }

    public void initiateListener(String notificationKey, PhoenixNotification phoenixNotification)
    {
        if (notificationKey==null)
            throw new NullPointerException("Notification key must not be null");
        if (phoenixNotification==null)
            throw new NullPointerException("Notification listener must not be null");
        synchronized (waitObject) {
            CoreQueue notificationHandlerList = null;
            if (handlerList.containsKey(notificationKey))
                notificationHandlerList = handlerList.get(notificationKey);
            if (notificationHandlerList == null)
                return;
            notificationHandlerList.doQueue(notificationKey, phoenixNotification);
        }
    }

    public void initiateSingleListener(String notificationKey, PhoenixNotification phoenixNotification)
    {
        if (notificationKey==null)
            throw new NullPointerException("Notification key must not be null");
        if (phoenixNotification==null)
            throw new NullPointerException("Notification listener must not be null");
        synchronized (waitObject) {
            CoreQueue notificationHandlerList = null;
            if (handlerList.containsKey(notificationKey))
                notificationHandlerList = handlerList.get(notificationKey);
            if (notificationHandlerList == null)
                return;
            notificationHandlerList.doQueueSingle(notificationKey, phoenixNotification);
        }
    }
}
