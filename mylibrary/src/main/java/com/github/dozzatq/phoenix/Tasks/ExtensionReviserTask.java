package com.github.dozzatq.phoenix.Tasks;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by dxfb on 27.05.2017.
 */

class ExtensionReviserTask<PResult, PExtension> implements OnExtensionListener<PResult>,
        OnFailureListener,
        OnCompleteListener<PExtension> {

    private Executor executor;
    private Extension<PResult, Task<PExtension>> extensionTask;
    private Task<PExtension> pExtension;
    private final Object waitObject = new Object();

    public ExtensionReviserTask(Executor executor, Extension<PResult, Task<PExtension>> extensionTask, Task<PExtension> pExtension) {
        this.executor = executor;
        if (this.executor==null)
            this.executor = Tasks.getDefaultExecutor();
        this.extensionTask = extensionTask;
        this.pExtension = pExtension;
    }

    @Override
    public void OnExtension(final Task<PResult> pResult)
    {
        synchronized (waitObject) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    synchronized (waitObject) {
                        Task<PExtension> pExtensionTask = null;
                        try {
                            pExtensionTask = extensionTask.then(pResult);
                        } catch (Exception e) {
                            pExtension.setException(e);
                        }

                        if (pExtensionTask == null) {
                            ExtensionReviserTask.this.OnFailure(new NullPointerException("Extension returned null"));
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
