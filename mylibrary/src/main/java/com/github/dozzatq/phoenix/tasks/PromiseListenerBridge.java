package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

/**
 * Created by Rodion Bartoshyk on 30.07.2017.
 */

class PromiseListenerBridge implements OnTaskSuccessListener<Void> {

    private Promise promise;
    private OnPromiseListener promiseListener;
    private boolean keepSynced;

    PromiseListenerBridge(Promise promise, OnPromiseListener promiseListener, boolean keepSynced) {
        this.promise = promise;
        this.promiseListener = promiseListener;
        this.keepSynced = keepSynced;
    }

    @Override
    public void OnTaskSuccess(@NonNull Task task) {
        Promise promise = promiseListener.then();
        new PromiseListenerDecoration(promise, promiseListener, keepSynced);
    }
}
