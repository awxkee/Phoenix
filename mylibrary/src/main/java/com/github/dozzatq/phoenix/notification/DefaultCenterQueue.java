package com.github.dozzatq.phoenix.notification;

import android.support.annotation.NonNull;

import com.github.dozzatq.phoenix.tasks.MainThreadExecutor;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshyk on 04.06.2017.
 */

abstract class DefaultCenterQueue {

    protected ArrayDeque<NotificationSupplier<PhoenixNotification>> phoenixNotifications;
    protected final Object mLock = new Object();

    DefaultCenterQueue() {
        this.phoenixNotifications = new ArrayDeque<>();
    }

    void addNotification(@NonNull NotificationSupplier<PhoenixNotification> notification)
    {
        ExceptionThrower.throwIfSupplierNull(notification);
        synchronized (mLock) {
            phoenixNotifications.add(notification);
        }
    }

    int size()
    {
        synchronized (mLock)
        {
            ArrayDeque<PhoenixNotification> snapshot = new ArrayDeque<>();

            Iterator<NotificationSupplier<PhoenixNotification>> iterator = phoenixNotifications.iterator();
            while (iterator.hasNext())
            {
                NotificationSupplier<PhoenixNotification> notification = iterator.next();
                ExceptionThrower.throwIfSupplierNull(notification);
                if (notification.isDestroyed()) {
                    iterator.remove();
                    continue;
                }
                else if (notification.isStopped())
                {
                    continue;
                }
                else {
                    snapshot.push(notification.get());
                }
            }
            return snapshot.size();
        }
    }

    private PhoenixNotification tryResolveNotification(NotificationSupplier<PhoenixNotification> supplier,
                                                                     Iterator<NotificationSupplier<PhoenixNotification>> iterator)
    {
        if (supplier.isDestroyed()) {
            iterator.remove();
            return null;
        }
        else if (supplier.isStopped())
        {
            return null;
        }
        else {
            return supplier.get();
        }
    }

    ArrayDeque<PhoenixNotification> snap()
    {
        synchronized (mLock) {
            ArrayDeque<PhoenixNotification> snapshot = new ArrayDeque<>();

            Iterator<NotificationSupplier<PhoenixNotification>> iterator = phoenixNotifications.descendingIterator();
            while (iterator.hasNext()) {
                NotificationSupplier<PhoenixNotification> notification = iterator.next();
                ExceptionThrower.throwIfSupplierNull(notification);
                PhoenixNotification curNotification;
                if ((curNotification = tryResolveNotification(notification, iterator)) != null)
                    snapshot.push(curNotification);
            }
            return snapshot;
        }
    }

    void removeNotification(@NonNull PhoenixNotification notification)
    {
        ExceptionThrower.throwIfNotificationNull(notification);
        synchronized (mLock) {
            NotificationSupplier<PhoenixNotification> supplier = findPositionNotification(notification);
            if (supplier!=null)
                phoenixNotifications.remove(supplier);
        }
    }

    private void removeSupplier(@NonNull NotificationSupplier<PhoenixNotification> supplier)
    {
        ExceptionThrower.throwIfSupplierNull(supplier);
        synchronized (mLock) {
            if (phoenixNotifications.contains(supplier))
                phoenixNotifications.remove(supplier);
        }
    }

    private NotificationSupplier<PhoenixNotification> findPositionNotification(PhoenixNotification notification)
    {
        Iterator<NotificationSupplier<PhoenixNotification>> iterator = phoenixNotifications.descendingIterator();
        NotificationSupplier<PhoenixNotification> supplier = null;
        while (iterator.hasNext())
        {
            if ((supplier = iterator.next()) !=null && supplier.equals(notification))
                return supplier;
        }
        return null;
    }

    void clearQueue()
    {
        synchronized (mLock) {
            phoenixNotifications.clear();
        }
    }

    final void doCallForCurrent(final PhoenixNotification notification, final String notificationKey, final int delayed, final Object... values)
    {
        ExceptionThrower.throwIfNotificationNull(notification);
        synchronized (mLock) {
            throwIfQueueNull(phoenixNotifications);
            final NotificationSupplier<PhoenixNotification> supplier = findPositionNotification(notification);
            ExceptionThrower.throwIfSupplierNull(supplier);
            throwExecution(supplier, notificationKey, delayed, values);
        }
    }

    private void throwExecution(final NotificationSupplier<PhoenixNotification> supplier,
                                final String notificationKey,
                                final int delayed,
                                final Object... values )
    {
        if (supplier.isDestroyed()) {
            removeSupplier(supplier);
            return;
        }
        if (!supplier.isStopped()) {
            if (delayed <= 0) {
                supplier.execute(new Runnable() {
                    @Override
                    public void run() {
                        supplier.get().didReceiveNotification(notificationKey, values);
                        tryDeleteAfterSync(supplier);
                    }
                });
            } else {
                MainThreadExecutor.getInstance().executeDelayed(new Runnable() {
                    @Override
                    public void run() {
                        supplier.execute(new Runnable() {
                            @Override
                            public void run() {
                                supplier.get().didReceiveNotification(notificationKey, values);
                                tryDeleteAfterSync(supplier);
                            }
                        });
                    }
                }, delayed);
            }
        }
    }

    private boolean tryDeleteAfterSync( NotificationSupplier<PhoenixNotification> supplier)
    {
        if (!supplier.isSynced()) {
            removeSupplier(supplier);
            return true;
        }
        else
            return false;
    }

    final void doCallToHandler(String notificationKey)
    {
        ArrayDeque<PhoenixNotification> notifications = snap();
        synchronized (mLock)
        {
            throwIfQueueNull(phoenixNotifications);
            HandlerCore.getInstance().beginBatchedUpdate(notificationKey, notifications);
        }
    }

    final void doNativeCall(final String notificationKey, final int delayed, final Object... values)
    {
        synchronized (mLock)
        {
            throwIfQueueNull(phoenixNotifications);
            Iterator<NotificationSupplier<PhoenixNotification>> iterator = phoenixNotifications.descendingIterator();
            while (iterator.hasNext())
            {
                final NotificationSupplier<PhoenixNotification> supplier = iterator.next();
                ExceptionThrower.throwIfSupplierNull(supplier);
                throwExecution(supplier, notificationKey, delayed, values);
            }
        }
    }

    final void doCall(final String notificationKey, final int delayed, final Object... values)
    {
        synchronized (mLock)
        {
            throwIfQueueNull(phoenixNotifications);
            if (HandlerCore.getInstance().hasHandler(notificationKey)) {
                doCallToHandler(notificationKey);
                return;
            }
        }
        doNativeCall(notificationKey, delayed, values);
    }

    private void throwIfQueueNull(ArrayDeque<NotificationSupplier<PhoenixNotification>> arrayDeque)
    {
        if (arrayDeque==null)
            throw new NullPointerException("Queue is null. Something goes wrong!");
    }

}
