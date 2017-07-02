package com.github.dozzatq.phoenix.notification;

import com.github.dozzatq.phoenix.activity.CallbackSupplier;
import com.github.dozzatq.phoenix.activity.StreetPolice;

/**
 * Created by Rodion Bartoshyk on 02.07.2017.
 */

class NotificationSupplier<T> extends CallbackSupplier<T> {
    private boolean synced;
    public NotificationSupplier(T object, StreetPolice streetPolice, boolean synced) {
        super(object, streetPolice);
        this.synced = synced;
    }

    public boolean isSynced() {
        synchronized (mLock) {
            return synced;
        }
    }
}
