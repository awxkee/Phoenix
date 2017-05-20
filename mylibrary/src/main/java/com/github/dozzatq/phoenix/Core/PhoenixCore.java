package com.github.dozzatq.phoenix.Core;

import android.content.Context;
import android.support.annotation.AnyThread;
import android.util.Log;

import com.github.dozzatq.phoenix.Phoenix;
import com.github.dozzatq.phoenix.Notification.PhoenixNotification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dxfb on 08.12.2016.
 */

public class PhoenixCore {

    private static PhoenixCore ourInstance = null;

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

    private Map<String, List<NotificationHandler>> handlerList;

    private PhoenixCore()
    {
        if (Phoenix.getInstance().getContext()==null)
            throw  new IllegalStateException("Phoenix must be inited !");
        handlerList = Collections.synchronizedMap(new HashMap<String, List<NotificationHandler>>());
    }

    public PhoenixCore addNotificationHandler(String notificationKey, NotificationHandler handler)
    {
        if (notificationKey==null)
            throw new NullPointerException("Notification key must not be null");
        List<NotificationHandler> notificationHandlerList=null;
        if (handlerList.containsKey(notificationKey))
            notificationHandlerList = handlerList.get(notificationKey);
        else notificationHandlerList = Collections.synchronizedList(new ArrayList<NotificationHandler>());
        notificationHandlerList.add(handler);
        handlerList.put(notificationKey, notificationHandlerList);
        return this;
    }

    public PhoenixCore removeNotificationHandler(String notificationKey, NotificationHandler handler)
    {
        if (notificationKey==null)
            throw new NullPointerException("Notification key must not be null");
        List<NotificationHandler> notificationHandlerList=null;
        if (handlerList.containsKey(notificationKey))
            notificationHandlerList = handlerList.get(notificationKey);
        if (notificationHandlerList==null)
            return this;
        if (notificationHandlerList.contains(handler))
            notificationHandlerList.remove(handler);
        return this;
    }

    public void initiateListener(String notificationKey, PhoenixNotification phoenixNotification)
    {
        if (notificationKey==null)
            throw new NullPointerException("Notification key must not be null");
        if (phoenixNotification==null)
            throw new NullPointerException("Notification listener must not be null");
        List<NotificationHandler> notificationHandlerList=null;
        if (handlerList.containsKey(notificationKey))
            notificationHandlerList = handlerList.get(notificationKey);
        if (notificationHandlerList==null)
            return;
        for (NotificationHandler handler : notificationHandlerList) {
            try {
                handler.didNeedNotification(notificationKey, phoenixNotification);
            }
            catch (Exception e)
            {
                Log.d("PhoenixCore", "Bad Global Handler");
            }
        }
    }

    public void initiateSingleListener(String notificationKey, PhoenixNotification phoenixNotification)
    {
        if (notificationKey==null)
            throw new NullPointerException("Notification key must not be null");
        if (phoenixNotification==null)
            throw new NullPointerException("Notification listener must not be null");
        List<NotificationHandler> notificationHandlerList=null;
        if (handlerList.containsKey(notificationKey))
            notificationHandlerList = handlerList.get(notificationKey);
        if (notificationHandlerList==null)
            return;
        for (NotificationHandler handler : notificationHandlerList) {
            try {
                handler.didNeedNotificationSingle(notificationKey, phoenixNotification);
            }
            catch (Exception e)
            {
                Log.d("PhoenixCore", "Bad Global Handler");
            }
        }
    }
}
