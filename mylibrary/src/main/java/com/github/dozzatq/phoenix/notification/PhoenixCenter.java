package com.github.dozzatq.phoenix.notification;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.dozzatq.phoenix.activity.ActivityConnectorStrategy;
import com.github.dozzatq.phoenix.activity.ActivitySupplier;
import com.github.dozzatq.phoenix.activity.CallbackSupplier;
import com.github.dozzatq.phoenix.activity.StreetPolice;
import com.github.dozzatq.phoenix.core.PhoenixCore;
import com.github.dozzatq.phoenix.tasks.MainThreadExecutor;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshyk on 06.12.2016.
 */

public class PhoenixCenter {
    private static PhoenixCenter ourInstance = null;

    private Map<String, CenterQueue> notificationMap;
    private ArrayDeque<CenterAction> actionQueue;

    private final Object mLock = new Object();

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
        return addAction(null,MainThreadExecutor.getInstance(), actionKey, actionComplete, new StreetPolice() {
            @Override
            public void onDestroy() {
                destroy();
            }
        }, true);
    }

    @AnyThread
    public PhoenixCenter addAction(@NonNull String actionKey, @NonNull OnActionComplete actionComplete, boolean synced) {
        return addAction(null,MainThreadExecutor.getInstance(), actionKey, actionComplete, new StreetPolice() {
            @Override
            public void onDestroy() {
                destroy();
            }
        }, synced);
    }

    @AnyThread
    public PhoenixCenter addAction(@Nullable Activity activity, @NonNull String actionKey, @NonNull OnActionComplete actionComplete, @NonNull StreetPolice streetPolice,
                                   boolean synced) {
        return addAction(activity, MainThreadExecutor.getInstance(), actionKey, actionComplete, streetPolice, synced);
    }


    @AnyThread
    public PhoenixCenter addAction(@Nullable Activity activity, @NonNull String actionKey, @NonNull OnActionComplete actionComplete, @NonNull StreetPolice streetPolice) {
        return addAction(activity, MainThreadExecutor.getInstance(), actionKey, actionComplete, streetPolice, true);
    }

    @AnyThread
    public PhoenixCenter addAction(@Nullable Activity activity,
                                   @NonNull Executor executor,
                                   @NonNull String actionKey,
                                   @NonNull OnActionComplete actionComplete,
                                   @NonNull StreetPolice streetPolice,
                                   boolean synced)
    {
        ExceptionThrower.throwIfExecutorNull(executor);
        ExceptionThrower.throwIfActionNull(actionComplete);
        synchronized (mLock) {
            if (getAction(actionKey) != null)
                removeAction(actionKey);
            CenterAction centerAction = new CenterAction(executor, actionKey, actionComplete, streetPolice, synced);
            actionQueue.add(centerAction);
            if (activity!=null)
                CallbackActivitySupplier.getInstance(activity).addListener(centerAction.getAction());
            return this;
        }
    }

    @AnyThread
    public PhoenixCenter callAction(@NonNull String actionKey, @Nullable Object...values)
    {
        synchronized (mLock) {
            CenterAction action = getAction(actionKey);
            if (action!=null) {
                action.doCall(values);
                if (!action.getAction().isSynced())
                    removeAction(actionKey);
            }
            return this;
        }
    }

    @AnyThread
    public PhoenixCenter removeAction(@NonNull String actionKey)
    {
        ExceptionThrower.throwIfQueueKeyNull(actionKey);
        synchronized (mLock) {
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

    private CenterAction getAction(@NonNull String actionKey)
    {
        ExceptionThrower.throwIfQueueKeyNull(actionKey);
        synchronized (mLock) {
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
    public ArrayDeque<PhoenixNotification> getSnapshot(@NonNull final String notificationKey)
    {
        ArrayDeque<PhoenixNotification> snap = new ArrayDeque<>();
        if (notificationMap.containsKey(notificationKey)) {
            if (!notificationMap.isEmpty()) {
                if (notificationMap.containsKey(notificationKey)) {
                    DefaultCenterQueue queue = notificationMap.get(notificationKey);
                    snap = queue.snap();
                }
            }
        }
        return snap;
    }

    @AnyThread
    public int getNotificationsCount(@NonNull final String notificationKey)
    {
        if (!notificationMap.containsKey(notificationKey))
            return 0;
        if (!notificationMap.isEmpty()) {
            if (notificationMap.containsKey(notificationKey)) {
                DefaultCenterQueue queue = notificationMap.get(notificationKey);
                return queue.size();
            }
        }
        return 0;
    }

    @AnyThread
    public void postNotification(@NonNull final String notificationKey,@Nullable final Object... values)
    {
        postNotificationDelayed(notificationKey, 0, values);
    }

    @AnyThread
    public void postNotificationForEventListenerDelayed(@NonNull final String notificationKey,
                                                        @NonNull final PhoenixNotification phoenixNotification,
                                                        int delayed,
                                                        @Nullable final Object... values)
    {
        ExceptionThrower.throwIfNotificationNull(phoenixNotification);
        ExceptionThrower.throwIfQueueKeyNull(notificationKey);
        synchronized (mLock) {
            if (!notificationMap.containsKey(notificationKey))
                return;
            if (!notificationMap.isEmpty()) {
                if (notificationMap.containsKey(notificationKey)) {
                    DefaultCenterQueue phoenixNotifications = notificationMap.get(notificationKey);
                    phoenixNotifications.doCallForCurrent(phoenixNotification,notificationKey, delayed, values);
                }
            }
        }
    }

    @AnyThread
    public void postNotificationDelayed(@NonNull final String notificationKey, @NonNull int delay, @NonNull final Object... values)
    {
        ExceptionThrower.throwIfQueueKeyNull(notificationKey);
        synchronized (mLock) {
            if (!notificationMap.containsKey(notificationKey))
                return;
            if (!notificationMap.isEmpty()) {
                if (notificationMap.containsKey(notificationKey)) {
                    DefaultCenterQueue phoenixNotifications = notificationMap.get(notificationKey);
                    phoenixNotifications.doCall(notificationKey, delay, values);
                }
            }
        }
    }

    @AnyThread
    public PhoenixCenter removeAllListeners(@NonNull String notificationKey)
    {
        ExceptionThrower.throwIfQueueKeyNull(notificationKey);
        synchronized (mLock) {

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
    public void executeHandler(@NonNull String notificationKey)
    {
        ExceptionThrower.throwIfQueueKeyNull(notificationKey);
        synchronized (mLock) {

            if (!notificationMap.containsKey(notificationKey))
                return;
            if (notificationMap.containsKey(notificationKey)) {
                DefaultCenterQueue notifications = notificationMap.get(notificationKey);
                notifications.doCallToHandler(notificationKey);
            }
        }
    }

    @AnyThread
    public PhoenixCenter removeListener(@NonNull String notificationKey,@NonNull PhoenixNotification phoenixNotification)
    {
        ExceptionThrower.throwIfQueueKeyNull(notificationKey);
        ExceptionThrower.throwIfNotificationNull(phoenixNotification);
        synchronized (mLock) {

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
    public PhoenixCenter addListener(@NonNull String notificationKey,
                                     @NonNull PhoenixNotification phoenixNotification)
    {
        return addListener(notificationKey, phoenixNotification, true);
    }

    @AnyThread
    public PhoenixCenter addListener(@NonNull String notificationKey,
                                     @NonNull PhoenixNotification phoenixNotification, boolean synced)
    {
        return addListener(null, MainThreadExecutor.getInstance(), notificationKey, phoenixNotification, new StreetPolice() {
            @Override
            public void onDestroy() {
            }
        }, synced);
    }


    @AnyThread
    public PhoenixCenter addListener(@Nullable Activity activity, @NonNull String notificationKey,
                                     @NonNull PhoenixNotification phoenixNotification)
    {
        return addListener(activity, MainThreadExecutor.getInstance(), notificationKey, phoenixNotification);
    }


    @AnyThread
    public PhoenixCenter addListener(@NonNull Executor executor, @NonNull String notificationKey,
                                     @NonNull PhoenixNotification phoenixNotification)
    {
        return addListener(null, executor, notificationKey, phoenixNotification);
    }

    @AnyThread
    public PhoenixCenter addListener(@Nullable Activity activity, @NonNull Executor executor, @NonNull String notificationKey,
                                     @NonNull PhoenixNotification phoenixNotification, @NonNull StreetPolice streetPolice,
                                     boolean syncedListener){
        ExceptionThrower.throwIfQueueKeyNull(notificationKey);
        ExceptionThrower.throwIfNotificationNull(phoenixNotification);
        ExceptionThrower.throwIfExecutorNull(executor);
        ExceptionThrower.throwIfStreetPolicyNull(streetPolice);
        synchronized (mLock) {

            CenterQueue observerList;
            if (notificationMap.containsKey(notificationKey)) {
                observerList = notificationMap.get(notificationKey);
            } else {
                observerList = new CenterQueue(executor);
                notificationMap.put(notificationKey, (CenterQueue) observerList);
            }
            NotificationSupplier<PhoenixNotification> callbackSupplier = new NotificationSupplier<>(phoenixNotification, streetPolice, syncedListener);
            observerList.addNotification(callbackSupplier);
            if (activity!=null)
                CallbackActivitySupplier.getInstance(activity).addListener(callbackSupplier);
            PhoenixCore.getInstance().initiateListener(notificationKey, phoenixNotification);
            return this;
        }
    }

    @AnyThread
    public PhoenixCenter addListener(@Nullable Activity activity, @NonNull Executor executor, @NonNull String notificationKey,
                                     @NonNull PhoenixNotification phoenixNotification)
    {
        return addListener(activity, executor, notificationKey, phoenixNotification, new StreetPolice() {
            @Override
            public void onDestroy() {
                destroy();
            }
        }, true);
    }

    @AnyThread
    public PhoenixCenter addListener(@Nullable Activity activity, @NonNull String notificationKey,
                                     @NonNull PhoenixNotification phoenixNotification, @NonNull StreetPolice streetPolice)
    {
        return addListener(activity, MainThreadExecutor.getInstance(), notificationKey, phoenixNotification, streetPolice, true);
    }

    private PhoenixCenter()
    {
        notificationMap = new HashMap<String, CenterQueue>();
        actionQueue = new ArrayDeque<>();
    }

    static class CallbackActivitySupplier extends ActivitySupplier {
        private final List<WeakReference<CallbackSupplier>> mListeners = new ArrayList<>();

        private static CallbackActivitySupplier getInstance(Activity activity)
        {
            CallbackActivitySupplier callbackActivitySupplier;
            ActivityConnectorStrategy activityConnectorStrategy;
            if ((callbackActivitySupplier = (activityConnectorStrategy = ActivityConnectorStrategy.connect(activity))
                    .tryGetSupplier("NotificationActivityCallback", CallbackActivitySupplier.class))==null){
                callbackActivitySupplier = new CallbackActivitySupplier(activityConnectorStrategy);
            }

            return callbackActivitySupplier;
        }

        private CallbackActivitySupplier(ActivityConnectorStrategy activityConnectorStrategy)
        {
            super(activityConnectorStrategy);
            activityConnectorStrategy.addListenerInterface("NotificationActivityCallback", this);
        }

        void addListener(CallbackSupplier callbackSupplier)
        {
            synchronized (mListeners)
            {
                mListeners.add(new WeakReference<CallbackSupplier>(callbackSupplier));
            }
        }

        @Override
        public void onDestroy() {
            synchronized (mListeners) {
                for (WeakReference<CallbackSupplier> mListener : mListeners) {
                    CallbackSupplier supplier = mListener.get();
                    if (supplier != null)
                        supplier.getStreetPolice().onDestroy();
                }
                mListeners.clear();
            }
        }

        @Override
        public void onStart() {
            synchronized (mListeners) {
                for (WeakReference<CallbackSupplier> mListener : mListeners) {
                    CallbackSupplier supplier = mListener.get();
                    if (supplier != null)
                        supplier.getStreetPolice().onStart();
                }
            }
        }

        @Override
        public void onStop() {
            synchronized (mListeners) {
                for (WeakReference<CallbackSupplier> mListener : mListeners) {
                    CallbackSupplier supplier = mListener.get();
                    if (supplier != null)
                        supplier.getStreetPolice().onStop();
                }
            }
        }
    }
}
