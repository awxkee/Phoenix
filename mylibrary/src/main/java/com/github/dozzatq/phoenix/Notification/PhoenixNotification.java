package com.github.dozzatq.phoenix.Notification;

import android.support.annotation.MainThread;
import android.support.annotation.UiThread;

/**
 * Created by dxfb on 06.12.2016.
 */

public interface PhoenixNotification {
    @UiThread
    @MainThread
    public void didReceiveNotification(String notification, Object... values);
}
