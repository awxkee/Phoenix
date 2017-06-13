package com.github.dozzatq.phoenix.Notification56;

import android.content.Context;
import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.dozzatq.phoenix.Core56.PhoenixCore;
import com.github.dozzatq.phoenix.Phoenix;
import com.github.dozzatq.phoenix.Tasks56.DefaultExecutor;
import com.github.dozzatq.phoenix.Util67.PhoenixUtilities;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by dxfb on 06.12.2016.
 */

public class PhoenixCenter {
    private static PhoenixCenter ourInstance = null;

    private Map<String, CenterQueue> notificationMap;
    private Map<String, SingleCenterQueue> singleNotificationMap;
    private ArrayDeque<CenterAction> actionQueue;

    private final Object waitObject = new Object();

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

    @AnyThread
    public PhoenixCenter addAction(@NonNull String actionKey, @NonNull OnActionComplete actionComplete) {
        return addAction(DefaultExecutor.getInstance(), actionKey, actionComplete);
    }

    @AnyThread
    public PhoenixCenter addAction(@NonNull Executor executor, @NonNull String actionKey, @NonNull OnActionComplete actionComplete)
    {
        synchronized (waitObject) {
            if (actionKey == null || actionComplete == null)
                throw new NullPointerException("Action & OnActionComplete must not be null!");
            if (getAction(actionKey) != null)
                removeAction(actionKey);
            actionQueue.add(new CenterAction(executor, actionKey, actionComplete));
            return this;
        }
    }

    @AnyThread
    public PhoenixCenter callAction(@NonNull String actionKey, @Nullable Object...values)
    {
        synchronized (waitObject) {
            CenterAction action = getAction(actionKey);
            if (action!=null)
                action.doCall(values);
            return this;
        }
    }

    @AnyThread
    public PhoenixCenter removeAction(@NonNull String actionKey)
    {
        synchronized (waitObject) {
            if (actionKey == null)
                throw new NullPointerException("Action must not be null!");
            Iterator<CenterAction> centerActionIterator = actionQueue.descendingIterator();
            while (centerActionIterator.hasNext()) {
                CenterAction currentAction = centerActionIterator.next();
                if (currentAction.isAction(actionKey)) {
                    centerActionIterator.remove();
                    break;
                }
            }
            return this;
        }
    }

    private CenterAction getAction(String actionKey)
    {
        synchronized (waitObject) {
            if (actionKey == null)
                throw new NullPointerException("Action must not be null!");
            Iterator<CenterAction> centerActionIterator = actionQueue.descendingIterator();
            CenterAction resultAction = null;
            while (centerActionIterator.hasNext()) {
                CenterAction currentAction = centerActionIterator.next();
                if (currentAction.isAction(actionKey)) {
                    resultAction = currentAction;
                    break;
                }
            }
            return resultAction;
        }
    }

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
        synchronized (waitObject) {
            if (phoenixNotification == null)
                throw new NullPointerException("Notification listener must not be null");
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
    }

    @AnyThread
    public void postNotificationForSingleEventListener(final String notificationKey, final PhoenixNotification phoenixNotification, final Object... values)
    {
        postNotificationForSingleEventListenerDelayed(notificationKey, phoenixNotification, 0, values);
    }

    @AnyThread
    public void postNotificationForSingleEventListenerDelayed(final String notificationKey, final PhoenixNotification phoenixNotification, int delayed, final Object... values)
    {
        synchronized (waitObject) {
            if (phoenixNotification == null)
                throw new NullPointerException("Notification listener must not be null");
            if (!notificationMap.isEmpty()) {
                if (notificationMap.containsKey(notificationKey)) {
                    DefaultCenterQueue singleList = singleNotificationMap.get(notificationKey);
                    singleList.doCallForCurrent(phoenixNotification, notificationKey, delayed, values);
                }
            }
        }
    }

    @AnyThread
    public void postNotificationSingleEventListeners(final String notificationKey, final Object... values)
    {
        postNotificationSingleEventListenersDelayed(notificationKey,0,values);
    }

    @AnyThread
    public void postNotificationSingleEventListenersDelayed(final String notificationKey, int delay, final Object... values)
    {
        synchronized (waitObject) {
            if (notificationKey == null)
                throw new NullPointerException("Notification key must not be null");

            if (!singleNotificationMap.isEmpty()) {
                if (singleNotificationMap.containsKey(notificationKey)) {
                    DefaultCenterQueue phoenixNotifications = singleNotificationMap.get(notificationKey);
                    phoenixNotifications.doCall(notificationKey, delay, values);
                }
            }
        }
    }

    @AnyThread
    public void postNotificationDelayed(final String notificationKey, int delay, final Object... values)
    {
        synchronized (waitObject) {
            if (notificationKey == null)
                throw new NullPointerException("Notification key must not be null");
            if (!notificationMap.containsKey(notificationKey) && !singleNotificationMap.containsKey(notificationKey))
                return;
            if (!notificationMap.isEmpty()) {
                if (notificationMap.containsKey(notificationKey)) {
                    DefaultCenterQueue phoenixNotifications = notificationMap.get(notificationKey);
                    phoenixNotifications.doCall(notificationKey, delay, values);
                }
            }
            postNotificationSingleEventListenersDelayed(notificationKey, delay, values);
        }
    }

    @AnyThread
    public PhoenixCenter removeAllListeners(String notificationKey)
    {
        synchronized (waitObject) {
            if (notificationKey == null)
                throw new NullPointerException("Notification key must not be null");

            if (notificationMap.isEmpty())
                return this;

            if (!notificationMap.containsKey(notificationKey)) {
                return this;
            } else {
                DefaultCenterQueue observerList = notificationMap.get(notificationKey);
                observerList.flushQueue();
            }

            return this;
        }
    }

    @AnyThread
    public void executeHandler(String notificationKey)
    {
        synchronized (waitObject) {
            if (notificationKey == null)
                throw new NullPointerException("Notification key must not be null");

            if (!notificationMap.containsKey(notificationKey) && !singleNotificationMap.containsKey(notificationKey))
                return;
            if (notificationMap.containsKey(notificationKey)) {
                DefaultCenterQueue notifications = notificationMap.get(notificationKey);
                notifications.doCallToHandler(notificationKey);
            }
            if (singleNotificationMap.containsKey(notificationKey)) {
                DefaultCenterQueue notifications = singleNotificationMap.get(notificationKey);
                notifications.doCallToHandler(notificationKey);
            }
        }
    }

    @AnyThread
    public void clearCenterForKey(String notificationKey)
    {
        if (notificationKey == null)
            throw new NullPointerException("Notification key must not be null");

        removeAllListeners(notificationKey);
        removeAllSingleEventListeners(notificationKey);
    }

    @AnyThread
    public PhoenixCenter removeAllSingleEventListeners(String notificationKey)
    {
        synchronized (waitObject) {
            if (notificationKey == null)
                throw new NullPointerException("Notification key must not be null");


            if (singleNotificationMap.isEmpty())
                return this;

            if (!singleNotificationMap.containsKey(notificationKey)) {
                return this;
            } else {
                DefaultCenterQueue observerList = singleNotificationMap.get(notificationKey);
                observerList.flushQueue();
            }

            return this;
        }
    }


    @AnyThread
    public PhoenixCenter removeListener(String notificationKey, PhoenixNotification phoenixNotification)
    {
        synchronized (waitObject) {
            if (phoenixNotification == null)
                throw new NullPointerException("PhoenixNotification must not be null");

            if (notificationKey == null)
                throw new NullPointerException("Notification key must not be null");

            if (notificationMap.isEmpty())
                return this;

            if (!notificationMap.containsKey(notificationKey)) {
                return this;
            } else {
                DefaultCenterQueue observerList = notificationMap.get(notificationKey);
                observerList.removeNotification(phoenixNotification);
            }

            return this;
        }
    }

    @AnyThread
    public PhoenixCenter removeSingleEventListener(String notificationKey, PhoenixNotification phoenixNotification)
    {
        synchronized (waitObject) {
            if (phoenixNotification == null)
                throw new NullPointerException("PhoenixNotification must not be null");

            if (notificationKey == null)
                throw new NullPointerException("Notification key must not be null");


            if (singleNotificationMap.isEmpty())
                return this;

            if (!singleNotificationMap.containsKey(notificationKey)) {
                return this;
            } else {
                DefaultCenterQueue observerList = singleNotificationMap.get(notificationKey);
                observerList.removeNotification(phoenixNotification);
            }

            return this;
        }
    }

    @AnyThread
    public PhoenixCenter addListener(@NonNull String notificationKey,
                                     @NonNull PhoenixNotification phoenixNotification)
    {
        return addListener(DefaultExecutor.getInstance(), notificationKey, phoenixNotification);
    }

    @AnyThread
    public PhoenixCenter addListener(@NonNull Executor executor, @NonNull String notificationKey,
                                     @NonNull PhoenixNotification phoenixNotification)
    {
        synchronized (waitObject) {
            if (phoenixNotification == null)
                throw new NullPointerException("PhoenixNotification must not be null");

            if (notificationKey == null)
                throw new NullPointerException("Notification key must not be null");

            CenterQueue observerList;
            if (notificationMap.containsKey(notificationKey)) {
                observerList = notificationMap.get(notificationKey);
            } else {
                observerList = new CenterQueue(executor);
                notificationMap.put(notificationKey, (CenterQueue) observerList);
            }
            observerList.addNotification(phoenixNotification);
            PhoenixCore.getInstance().initiateListener(notificationKey, phoenixNotification);

            return this;
        }
    }

    @AnyThread
    public PhoenixCenter addListenerForSingleEvent(@NonNull String notificationKey,
                                                   @NonNull PhoenixNotification phoenixNotification) {
        return addListenerForSingleEvent(DefaultExecutor.getInstance(), notificationKey, phoenixNotification);
    }

    @AnyThread
    public PhoenixCenter addListenerForSingleEvent(@NonNull Executor executor,
                                                   @NonNull String notificationKey,
                                                   @NonNull PhoenixNotification phoenixNotification)
    {
        synchronized (waitObject) {
            if (phoenixNotification == null)
                throw new NullPointerException("PhoenixNotification must not be null");

            if (notificationKey == null)
                throw new NullPointerException("Notification key must not be null");

            SingleCenterQueue observerList;
            if (singleNotificationMap.containsKey(notificationKey)) {
                observerList = singleNotificationMap.get(notificationKey);
            } else {
                observerList = new SingleCenterQueue(executor);
                singleNotificationMap.put(notificationKey, observerList);
            }
            observerList.addNotification(phoenixNotification);
            PhoenixCore.getInstance().initiateSingleListener(notificationKey, phoenixNotification);

            return this;
        }
    }

    private PhoenixCenter()
    {
        Context appContext = Phoenix.getInstance().getContext();
        if (appContext==null)
            throw new IllegalStateException("Phoenix must be inited !");
        notificationMap = new HashMap<String, CenterQueue>();
        actionQueue = new ArrayDeque<>();
        singleNotificationMap = new HashMap<String, SingleCenterQueue>();
    }
}
