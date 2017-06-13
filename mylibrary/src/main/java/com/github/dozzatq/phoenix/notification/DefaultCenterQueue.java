package com.github.dozzatq.phoenix.notification;

import android.support.annotation.NonNull;

import com.github.dozzatq.phoenix.core.PhoenixCore;
import com.github.dozzatq.phoenix.tasks.MainThreadExecutor;

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
        ExceptionThrower.throwIfNotificationNull(notification);
        synchronized (waitObject) {
            phoenixNotifications.add(notification);
        }
    }

    public void removeNotification(@NonNull PhoenixNotification notification)
    {
        ExceptionThrower.throwIfNotificationNull(notification);
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
        ExceptionThrower.throwIfNotificationNull(phoenixNotification);
        synchronized (waitObject)
        {
            ExceptionThrower.throwIfExecutorNull(queueExecutor);
            throwIfQueueNull(phoenixNotifications);
            if (delayed<=0) {
                    queueExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            phoenixNotification.didReceiveNotification(notificationKey, values);
                        }
                    });
                }
                else {
                    MainThreadExecutor.getInstance().executeDelayed(new Runnable() {
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
            ExceptionThrower.throwIfExecutorNull(queueExecutor);
            throwIfQueueNull(phoenixNotifications);
            Iterator<PhoenixNotification> iterator = phoenixNotifications.descendingIterator();
            while (iterator.hasNext())
            {
                PhoenixNotification notification = iterator.next();
                ExceptionThrower.throwIfNotificationNull(notification);
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
            ExceptionThrower.throwIfExecutorNull(queueExecutor);
            throwIfQueueNull(phoenixNotifications);
            Iterator<PhoenixNotification> iterator = phoenixNotifications.descendingIterator();
            while (iterator.hasNext())
            {
                final PhoenixNotification notification = iterator.next();
                ExceptionThrower.throwIfNotificationNull(notification);
                if (delayed<=0) {
                    queueExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            notification.didReceiveNotification(notificationKey, values);
                        }
                    });
                }
                else {
                    MainThreadExecutor.getInstance().executeDelayed(new Runnable() {
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

    private void throwIfQueueNull(ArrayDeque<PhoenixNotification> arrayDeque)
    {
        if (arrayDeque==null)
            throw new NullPointerException("Queue is null. Something goes wrong!");
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
