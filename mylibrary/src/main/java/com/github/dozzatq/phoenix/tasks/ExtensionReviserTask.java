package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshik on 27.05.2017.
 */

class ExtensionReviserTask<PResult, PExtension> implements OnTaskSuccessListener<PResult>,
        OnFailureListener,
        OnCompleteListener<PExtension> {

    private Executor executor;
    private Extension<PResult, Task<PExtension>> extensionTask;
    private Task<PExtension> pExtension;
    private final Object mLock = new Object();

    ExtensionReviserTask(Executor executor, Extension<PResult, Task<PExtension>> extensionTask, Task<PExtension> pExtension) {
        this.executor = executor;
        if (this.executor==null)
            this.executor = Tasks.getDefaultExecutor();
        this.extensionTask = extensionTask;
        this.pExtension = pExtension;
    }

    @Override
    public void OnTaskSuccess(@NonNull final Task<PResult> pResult)
    {
        synchronized (mLock) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    synchronized (mLock) {
                        Task<PExtension> pExtensionTask = null;
                        try {
                            pExtensionTask = extensionTask.then(pResult);
                        } catch (Exception e) {
                            pExtension.setException(e);
                        }

                        if (pExtensionTask == null) {
                            ExtensionReviserTask.this.OnFailure(new NullPointerException("Extension task returned null"));
                        } else {
                            pExtensionTask.addOnCompleteListener(ExtensionReviserTask.this);
                            pExtensionTask.addOnFailureListener(ExtensionReviserTask.this);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void OnFailure(@NonNull Exception exception) {
        pExtension.setException(exception);
    }

    @Override
    public void OnComplete(PExtension pExtension) {
        ExtensionReviserTask.this.pExtension.setResult(pExtension);
    }
}
