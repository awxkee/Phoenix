package com.github.dozzatq.phoenix.Notification;

import android.support.annotation.NonNull;

import com.github.dozzatq.phoenix.Core.PhoenixCore;
import com.github.dozzatq.phoenix.Tasks.DefaultExecutor;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.Executor;

/**
 * Created by dxfb on 04.06.2017.
 */

abstract class DefaultCenterQueue {

    protected ArrayDeque<PhoenixNotification> phoenixNotifications;
    protected volatile boolean keepSynced;
    protected final Object waitObject = new Object();
    protected Executor queueExecutor;

    public DefaultCenterQueue(Executor queueExecutor) {
        this.queueExecutor = queueExecutor;
        this.phoenixNotifications = new ArrayDeque<>();
        keepSynced = true;
    }

    public void addNotification(@NonNull PhoenixNotification notification)
    {
        synchronized (waitObject) {
            phoenixNotifications.add(notification);
        }
    }

    public void removeNotification(@NonNull PhoenixNotification notification)
    {
        synchronized (waitObject) {
            if (phoenixNotifications.contains(notification))
                phoenixNotifications.remove(notification);
        }
    }

    public void flushQueue()
    {
        synchronized (waitObject) {
            phoenixNotifications.clear();
        }
    }

    public final void doCallForCurrent(final PhoenixNotification phoenixNotification, final String notificationKey, final int delayed, final Object...values)
    {
        synchronized (waitObject)
        {
            if (queueExecutor==null || phoenixNotifications==null)
                throw new NullPointerException("Executor and Queue must not be null!");
                if (phoenixNotification==null)
                    throw new NullPointerException("Notification must not be null!");
                if (delayed<=0) {
                    queueExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            phoenixNotification.didReceiveNotification(notificationKey, values);
                        }
                    });
                }
                else {
                    DefaultExecutor.getInstance().executeDelayed(new Runnable() {
                        @Override
                        public void run() {
                            queueExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    phoenixNotification.didReceiveNotification(notificationKey, values);
                                }
                            });
                        }
                    }, delayed);
            }
                if (!isSynced())
                    removeNotification(phoenixNotification);
            }
    }

    public final void doCallToHandler(String notificationKey)
    {
        synchronized (waitObject)
        {
            if (queueExecutor==null || phoenixNotifications==null)
                throw new NullPointerException("Executor and Queue must not be null!");
            Iterator<PhoenixNotification> iterator = phoenixNotifications.descendingIterator();
            while (iterator.hasNext())
            {
                PhoenixNotification notification = iterator.next();
                if (notification==null)
                    throw new NullPointerException("Notification must not be null!");
                PhoenixCore.getInstance().initiateListener(notificationKey, notification);
                if (!isSynced())
                    iterator.remove();
            }
        }
    }

    public final void doCall(final String notificationKey, final int delayed, final Object...values)
    {
        synchronized (waitObject)
        {
            if (queueExecutor==null || phoenixNotifications==null)
                throw new NullPointerException("Executor and Queue must not be null!");
            Iterator<PhoenixNotification> iterator = phoenixNotifications.descendingIterator();
            while (iterator.hasNext())
            {
                final PhoenixNotification notification = iterator.next();
                if (notification==null)
                    throw new NullPointerException("Notification must not be null!");
                if (delayed<=0) {
                    queueExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            notification.didReceiveNotification(notificationKey, values);
                        }
                    });
                }
                else {
                    DefaultExecutor.getInstance().executeDelayed(new Runnable() {
                        @Override
                        public void run() {
                            queueExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    notification.didReceiveNotification(notificationKey, values);
                                }
                            });
                        }
                    }, delayed);
                }
                if (!isSynced())
                    iterator.remove();
            }
        }
    }

    public final boolean isSynced() {
        synchronized (waitObject) {
            return keepSynced;
        }
    }

    public final void keepSynced(boolean keepSynced) {
        synchronized (waitObject) {
            this.keepSynced = keepSynced;
        }
    }
}
