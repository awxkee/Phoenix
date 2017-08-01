package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

/**
 * Created by Rodion Bartoshyk on 30.07.2017.
 */

class PromiseListenerDecoration implements OnTaskSuccessListener<Void>, OnFailureListener {

    private Promise completionPromise;
    private OnPromiseListener onPromiseListener;
    private boolean keepSynced;

    PromiseListenerDecoration(Promise completionPromise, OnPromiseListener onPromiseListener, boolean keepSynced) {
        this.completionPromise = completionPromise;
        this.onPromiseListener = onPromiseListener;
        this.keepSynced = keepSynced;
        completionPromise.addOnTaskSuccessListener(this, keepSynced);
        completionPromise.addOnFailureListener(this, keepSynced);
    }

    @Override
    public void OnTaskSuccess(@NonNull Task<Void> voidTask) {
        completionPromise.setResult(null);
    }

    @Override
    public void OnFailure(@NonNull Exception exception) {
        completionPromise.setException(exception);
    }
}
