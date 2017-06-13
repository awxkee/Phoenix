package com.github.dozzatq.phoenix.core;

import android.support.annotation.NonNull;

import com.github.dozzatq.phoenix.notification.PhoenixNotification;

/**
 * Created by dxfb on 08.12.2016.
 */

public interface NotificationHandler {
    public void didNeedNotification(@NonNull String key, @NonNull PhoenixNotification phoenixNotification);
    public void didNeedNotificationSingle(@NonNull String key, @NonNull PhoenixNotification phoenixNotification);
}
