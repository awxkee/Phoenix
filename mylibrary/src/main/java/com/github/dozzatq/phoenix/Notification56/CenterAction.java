package com.github.dozzatq.phoenix.Notification56;

import java.util.concurrent.Executor;

/**
 * Created by dxfb on 04.06.2017.
 */

class CenterAction {
    private String actionKey;
    private OnActionComplete actionComplete;
    private Executor executor;
    private final Object waitObject = new Object();

    CenterAction(Executor executor, String actionKey, OnActionComplete actionComplete) {
        this.actionKey = actionKey;
        this.actionComplete = actionComplete;
        this.executor = executor;
    }

    public void doCall(final Object... params)
    {
        synchronized (waitObject) {
            if (actionKey == null || actionComplete == null)
                throw new NullPointerException("Action and Callback must not be null!");
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    actionComplete.OnAction(actionKey, params);
                }
            });
        }
    }

    public boolean isAction(String actionKey)
    {
        synchronized (waitObject) {
            return this.actionKey.equals(actionKey);
        }
    }
}
