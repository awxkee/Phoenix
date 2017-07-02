package com.github.dozzatq.phoenix.notification;

import com.github.dozzatq.phoenix.activity.StreetPolice;

import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshyk on 04.06.2017.
 */

class CenterAction {
    private String actionKey;
    private NotificationSupplier<OnActionComplete> actionComplete;
    private Executor executor;
    private final Object mLock = new Object();

    NotificationSupplier<OnActionComplete> getAction()
    {
        synchronized (mLock) {
            return actionComplete;
        }
    }

    CenterAction(Executor executor, String actionKey, OnActionComplete actionComplete, StreetPolice streetPolice, boolean synced) {
        this.actionKey = actionKey;
        this.actionComplete = new NotificationSupplier<>(actionComplete, streetPolice, synced);
        this.executor = executor;
    }

    public void doCall(final Object... params)
    {
        synchronized (mLock) {
            if (actionKey == null && actionComplete==null)
                throw new NullPointerException("Action must not be null!");
            if (!actionComplete.getStreetPolice().isStopped() && actionComplete.getStreetPolice().isDestroyed()) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        actionComplete.get().OnAction(actionKey, params);
                    }
                });
            }
        }
    }

    public boolean isAction(String actionKey)
    {
        synchronized (mLock) {
            return this.actionKey.equals(actionKey);
        }
    }
}
