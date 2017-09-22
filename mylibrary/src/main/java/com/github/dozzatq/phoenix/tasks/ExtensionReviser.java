package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshik on 28.05.2017.
 */

class ExtensionReviser<PResult, PExtension> implements OnTaskSuccessListener<PResult> {

    private Executor executor;
    private Extension<PResult, PExtension> extensionTask;
    private Task<PExtension> pExtension;
    private final Object mLock = new Object();

    ExtensionReviser(Executor executor, Extension<PResult, PExtension> extensionTask, Task<PExtension> pExtension) {
        this.executor = executor;
        if (this.executor==null)
            this.executor = Tasks.getDefaultExecutor();
        this.extensionTask = extensionTask;
        this.pExtension = pExtension;
    }

    @Override
    public void OnTaskSuccess(@NonNull final Task<PResult> pResult) {
        synchronized (mLock) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    synchronized (mLock) {
                        PExtension pExtensionTask = null;
                        try {
                            pExtensionTask = extensionTask.then(pResult);
                        } catch (Exception e) {
                            pExtension.setException(e);
                        }

                        pExtension.setResult(pExtensionTask);
                    }
                }
            });
        }
    }

}
