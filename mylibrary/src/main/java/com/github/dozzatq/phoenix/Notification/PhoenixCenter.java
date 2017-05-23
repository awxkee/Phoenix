package com.github.dozzatq.phoenix.Notification;

import android.content.Context;
import android.support.annotation.AnyThread;
import android.util.Log;

import com.github.dozzatq.phoenix.Core.PhoenixCore;
import com.github.dozzatq.phoenix.Phoenix;
import com.github.dozzatq.phoenix.Util.PhoenixUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dxfb on 06.12.2016.
 */

public class PhoenixCenter {
    private static PhoenixCenter ourInstance = null;

    private Map<String, List<PhoenixNotification>> notificationMap;
    private Map<String, List<PhoenixNotification>> singleNotificationMap;

    public static PhoenixCenter getInstance() {
        PhoenixCenter localInstance = ourInstance;
        if (localInstance == null) {
            synchronized (PhoenixCenter.class) {
                localInstance = ourInstance;
                if (localInstance == null) {
                    ourInstance = localInstance = new PhoenixCenter();
                }
            }
        }
        return localInstance;
    }

  //  private Context appContext;

    @AnyThread
    public void postNotification(final String notificationKey,final Object... values)
    {
        postNotificationDelayed(notificationKey, 0, values);
    }

    @AnyThread
    public void postNotificationForEventListener(final String notificationKey, final PhoenixNotification phoenixNotification, final Object... values)
    {
        postNotificationForEventListenerDelayed(notificationKey, phoenixNotification, 0, values);
    }

    @AnyThread
    public void postNotificationForEventListenerDelayed(final String notificationKey, final PhoenixNotification phoenixNotification, int delayed, final Object... values)
    {
        if (phoenixNotification==null)
            throw  new NullPointerException("Notification listener must not be null");
        PhoenixUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    phoenixNotification.didReceiveNotification(notificationKey, values);
                } catch (Exception e) {
                    Log.d("PhoenixCenter", "Bad Listener");
                }
            }
        }, delayed);
    }

    @AnyThread
    public void postNotificationForSingleEventListener(final String notificationKey, final PhoenixNotification phoenixNotification, final Object... values)
    {
        postNotificationForSingleEventListenerDelayed(notificationKey, phoenixNotification, 0, values);
    }

    @AnyThread
    public void postNotificationForSingleEventListenerDelayed(final String notificationKey, final PhoenixNotification phoenixNotification, int delayed, final Object... values)
    {
        if (phoenixNotification==null)
                throw  new NullPointerException("Notification listener must not be null");
        if (!notificationMap.isEmpty()) {
            if (notificationMap.containsKey(notificationKey)) {
                List<PhoenixNotification> singleList = singleNotificationMap.get(notificationKey);
                if (singleList.contains(phoenixNotification))
                    singleList.remove(phoenixNotification);
            }
        }
        PhoenixUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    phoenixNotification.didReceiveNotification(notificationKey, values);
                } catch (Exception e) {
                    Log.d("PhoenixCenter", "Bad Single Listener");
                }
            }
        }, delayed);
    }

    @AnyThread
    public void postNotificationSingleEventListeners(final String notificationKey, final Object... values)
    {
        postNotificationSingleEventListenersDelayed(notificationKey,0,values);
    }

    @AnyThread
    public void postNotificationSingleEventListenersDelayed(final String notificationKey, int delay, final Object... values)
    {
        if (notificationKey==null)
            throw  new NullPointerException("Notification key must not be null");

        if (!singleNotificationMap.isEmpty()) {
            if (singleNotificationMap.containsKey(notificationKey)) {
                List<PhoenixNotification> phoenixNotifications = singleNotificationMap.get(notificationKey);
                for (final PhoenixNotification notification : phoenixNotifications) {
                    PhoenixUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                notification.didReceiveNotification(notificationKey, values);
                            }
                            catch (Exception e)
                            {
                                Log.d("PhoenixCenter", "Bad Single Listener");
                            }
                        }
                    }, delay);
                }
                phoenixNotifications.clear();
            }
        }
    }

    @AnyThread
    public void postNotificationDelayed(final String notificationKey, int delay, final Object... values)
    {
        if (notificationKey==null)
            throw  new NullPointerException("Notification key must not be null");
        if (!notificationMap.containsKey(notificationKey) && !singleNotificationMap.containsKey(notificationKey))
            return;
        if (!notificationMap.isEmpty()) {
            if (notificationMap.containsKey(notificationKey)) {
                List<PhoenixNotification> phoenixNotifications = notificationMap.get(notificationKey);
                for (final PhoenixNotification notification : phoenixNotifications) {
                    PhoenixUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                notification.didReceiveNotification(notificationKey, values);
                            }
                            catch (Exception e)
                            {
                                Log.d("PhoenixCenter", "Bad Listener");
                            }
                        }
                    }, delay);
                }
            }
        }
        postNotificationSingleEventListenersDelayed(notificationKey, delay, values);
    }

    @AnyThread
    public PhoenixCenter removeAllListeners(String notificationKey)
    {
        if (notificationKey==null)
            throw  new NullPointerException("Notification key must not be null");

        List<PhoenixNotification> observerList;

        if (notificationMap.isEmpty())
            return this;

        if (!notificationMap.containsKey(notificationKey)) {
            return this;
        }
        else{
            observerList = notificationMap.get(notificationKey);
            if (observerList.isEmpty())
                return this;
            else {
               observerList.clear();
            }
        }

        return this;
    }

    @AnyThread
    public void executeHandler(String notificationKey)
    {
        if (notificationKey==null)
            throw  new NullPointerException("Notification key must not be null");

        if (!notificationMap.containsKey(notificationKey) && !singleNotificationMap.containsKey(notificationKey))
            return;
        if (notificationMap.containsKey(notificationKey)) {
            List<PhoenixNotification> notifications = notificationMap.get(notificationKey);
            for (PhoenixNotification notification : notifications) {
                try {
                    PhoenixCore.getInstance().initiateListener(notificationKey, notification);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (singleNotificationMap.containsKey(notificationKey))
        {
            List<PhoenixNotification> notifications = singleNotificationMap.get(notificationKey);
            for (PhoenixNotification notification : notifications) {
                try {
                    PhoenixCore.getInstance().initiateListener(notificationKey, notification);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            notifications.clear();
        }
    }

    @AnyThread
    public void clearCenterForKey(String notificationKey)
    {
        if (notificationKey==null)
            throw  new NullPointerException("Notification key must not be null");

        removeAllListeners(notificationKey);
        removeAllSingleEventListeners(notificationKey);
    }

    @AnyThread
    public PhoenixCenter removeAllSingleEventListeners(String notificationKey)
    {
        if (notificationKey==null)
            throw  new NullPointerException("Notification key must not be null");

        List<PhoenixNotification> observerList;

        if (singleNotificationMap.isEmpty())
            return this;

        if (!singleNotificationMap.containsKey(notificationKey)) {
            return this;
        }
        else{
            observerList = singleNotificationMap.get(notificationKey);
            if (observerList.isEmpty())
                return this;
            else {
                observerList.clear();
            }
        }

        return this;
    }


    @AnyThread
    public PhoenixCenter removeListener(String notificationKey, PhoenixNotification phoenixNotification)
    {
        if (phoenixNotification==null)
            throw new NullPointerException("PhoenixNotification must not be null");

        if (notificationKey==null)
            throw  new NullPointerException("Notification key must not be null");

        List<PhoenixNotification> observerList;

        if (notificationMap.isEmpty())
            return this;

        if (!notificationMap.containsKey(notificationKey)) {
            return this;
        }
        else{
            observerList = notificationMap.get(notificationKey);
            if (observerList.isEmpty())
                return this;
            else {
                if (observerList.contains(phoenixNotification))
                {
                    observerList.remove(phoenixNotification);
                    return this;
                }
            }
        }

        return this;
    }

    @AnyThread
    public PhoenixCenter removeSingleEventListener(String notificationKey, PhoenixNotification phoenixNotification)
    {
        if (phoenixNotification==null)
            throw new NullPointerException("PhoenixNotification must not be null");

        if (notificationKey==null)
            throw  new NullPointerException("Notification key must not be null");

        List<PhoenixNotification> observerList;

        if (singleNotificationMap.isEmpty())
            return this;

        if (!singleNotificationMap.containsKey(notificationKey)) {
            return this;
        }
        else{
            observerList = singleNotificationMap.get(notificationKey);
            if (observerList.isEmpty())
                return this;
            else {
                if (observerList.contains(phoenixNotification))
                {
                    observerList.remove(phoenixNotification);
                    return this;
                }
            }
        }

        return this;
    }

    @AnyThread
    public PhoenixCenter addListener(String notificationKey, PhoenixNotification phoenixNotification)
    {
        if (phoenixNotification==null)
            throw new NullPointerException("PhoenixNotification must not be null");

        if (notificationKey==null)
            throw  new NullPointerException("Notification key must not be null");

        List<PhoenixNotification> observerList;
        if (notificationMap.containsKey(notificationKey)) {
            observerList = notificationMap.get(notificationKey);
        }
        else{
            observerList = Collections.synchronizedList(new ArrayList<PhoenixNotification>());
            notificationMap.put(notificationKey, observerList);
        }
        observerList.add(phoenixNotification);
        PhoenixCore.getInstance().initiateListener(notificationKey, phoenixNotification);

        return this;
    }

    @AnyThread
    public PhoenixCenter addListenerForSingleEvent(String notificationKey, PhoenixNotification phoenixNotification)
    {
        if (phoenixNotification==null)
            throw new NullPointerException("PhoenixNotification must not be null");

        if (notificationKey==null)
            throw  new NullPointerException("Notification key must not be null");

        List<PhoenixNotification> observerList;
        if (singleNotificationMap.containsKey(notificationKey)) {
            observerList = singleNotificationMap.get(notificationKey);
        }
        else{
            observerList = Collections.synchronizedList(new ArrayList<PhoenixNotification>());
            singleNotificationMap.put(notificationKey, observerList);
        }
        observerList.add(phoenixNotification);
        PhoenixCore.getInstance().initiateSingleListener(notificationKey, phoenixNotification);

        return this;
    }

    private PhoenixCenter()
    {
        Context appContext = Phoenix.getInstance().getContext();
        if (appContext==null)
            throw new IllegalStateException("Phoenix must be inited !");
        notificationMap = Collections.synchronizedMap(new HashMap<String, List<PhoenixNotification>>());
        singleNotificationMap = Collections.synchronizedMap(new HashMap<String, List<PhoenixNotification>>());
    }
}
