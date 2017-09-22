package com.github.dozzatq.phoenix.notification;

import android.support.annotation.NonNull;

import com.github.dozzatq.phoenix.activity.StreetPolice;
import com.github.dozzatq.phoenix.notification.PhoenixCenter;
import com.github.dozzatq.phoenix.notification.PhoenixNotification;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * Created by Rodion Bartoshik on 08.12.2016.
 */

public abstract class NotificationHandler {

    public void batchNotification(@NonNull String key, @NonNull ArrayDeque<PhoenixNotification> phoenixNotifications)
    {
        Iterator<PhoenixNotification> notificationIterator = phoenixNotifications.descendingIterator();
        while (notificationIterator.hasNext())
        {
            PhoenixNotification notification = notificationIterator.next();
            if (notification!=null)
                didNeedNotification(key, notification);
        }
    }

    public abstract void didNeedNotification(@NonNull String key, @NonNull PhoenixNotification phoenixNotification);

    public final void whisper(@NonNull String key, @NonNull PhoenixNotification phoenixNotification, Object... values)
    {
        PhoenixCenter.getInstance().postNotificationForEventListenerDelayed(key, phoenixNotification, 0, values);
    }

    public final void whisper(@NonNull String key, @NonNull PhoenixNotification phoenixNotification, int delay, Object... values)
    {
        PhoenixCenter.getInstance().postNotificationForEventListenerDelayed(key, phoenixNotification, delay, values);
    }

    public final void shout(@NonNull String key, Object... values)
    {
        PhoenixCenter.getInstance().postPrivateNotificationDelayed(key, 0, values);
    }

    public final void shout(@NonNull String key, int delay, Object... values)
    {
        PhoenixCenter.getInstance().postPrivateNotificationDelayed(key, delay, values);
    }

    public final int look(@NonNull String key)
    {
        return PhoenixCenter.getInstance().getNotificationsCount(key);
    }

    @NonNull
    public final ArrayDeque<PhoenixNotification> snap(@NonNull String key)
    {
        return PhoenixCenter.getInstance().getSnapshot(key);
    }
}
