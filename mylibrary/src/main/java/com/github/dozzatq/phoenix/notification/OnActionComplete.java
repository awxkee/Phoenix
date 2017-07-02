package com.github.dozzatq.phoenix.notification;

import android.support.annotation.NonNull;

/**
 * Created by Rodion Bartoshyk on 04.06.2017.
 */

public interface OnActionComplete {
    public void OnAction(@NonNull String actionKey, @NonNull Object... values);
}
