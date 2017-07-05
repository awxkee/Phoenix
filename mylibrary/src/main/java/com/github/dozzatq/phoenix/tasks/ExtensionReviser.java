package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshyk on 28.05.2017.
 */

class ExtensionReviser<PResult, PExtension> implements OnTaskSuccessListener<PResult> {

    private Executor executor;
    private Extension<PResult, PExtension> extensionTask;
    private Task<PExtension> pExtension;
    private final Object waitObject = new Object();

    public ExtensionReviser(Executor executor, Extension<PResult, PExtension> extensionTask, Task<PExtension> pExtension) {
        this.executor = executor;
        if (this.executor==null)
            this.executor = Tasks.getDefaultExecutor();
        this.extensionTask = extensionTask;
        this.pExtension = pExtension;
    }

    @Override
    public void OnTaskSuccess(@NonNull final Task<PResult> pResult) {
        synchronized (waitObject) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    synchronized (waitObject) {
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
