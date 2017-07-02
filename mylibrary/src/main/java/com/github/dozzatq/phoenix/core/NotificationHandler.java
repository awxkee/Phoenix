package com.github.dozzatq.phoenix.core;

import android.support.annotation.NonNull;

import com.github.dozzatq.phoenix.notification.PhoenixCenter;
import com.github.dozzatq.phoenix.notification.PhoenixNotification;

import java.util.ArrayDeque;

/**
 * Created by Rodion Bartoshyk on 08.12.2016.
 */

public abstract class NotificationHandler {
    public abstract void didNeedNotification(@NonNull String key, @NonNull PhoenixNotification phoenixNotification);

    public final void shout(@NonNull String key, @NonNull PhoenixNotification phoenixNotification, Object... values)
    {
        PhoenixCenter.getInstance().postNotificationForEventListenerDelayed(key, phoenixNotification, 0, values);
    }

    public final void shout(@NonNull String key, @NonNull PhoenixNotification phoenixNotification, int delay, Object... values)
    {
        PhoenixCenter.getInstance().postNotificationForEventListenerDelayed(key, phoenixNotification, delay, values);
    }

    public int look(@NonNull String key)
    {
        return PhoenixCenter.getInstance().getNotificationsCount(key);
    }

    public ArrayDeque<PhoenixNotification> snap(@NonNull String key)
    {
        return PhoenixCenter.getInstance().getSnapshot(key);
    }
}
