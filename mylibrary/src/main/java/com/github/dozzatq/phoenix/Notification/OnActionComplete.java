package com.github.dozzatq.phoenix.Notification;

import android.support.annotation.NonNull;

/**
 * Created by dxfb on 04.06.2017.
 */

public interface OnActionComplete {
    public void OnAction(@NonNull String actionKey, @NonNull Object... values);
}
