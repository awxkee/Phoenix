package com.github.dozzatq.phoenix.Tasks;

import java.util.concurrent.Executor;

/**
 * Created by dxfb on 27.05.2017.
 */

public class ExtensionReviserTask<PResult, PExtension> implements OnExtensionListener<PResult>,
        OnFailureListener,
        OnSuccessListener<PExtension> {

    private Executor executor;
    private Extension<PResult, Task<PExtension>> extensionTask;
    private Task<PExtension> pExtension;

    public ExtensionReviserTask(Executor executor, Extension<PResult, Task<PExtension>> extensionTask, Task<PExtension> pExtension) {
        this.executor = executor;
        this.extensionTask = extensionTask;
        this.pExtension = pExtension;
    }

    @Override
    public void OnExtension(final Task<PResult> pResult) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Task<PExtension> pExtensionTask = null;
                try {
                    pExtensionTask = extensionTask.then(pResult);
                } catch (Exception e) {
                    pExtension.setException(e);
                }

                if (pExtensionTask==null)
                {
                    ExtensionReviserTask.this.OnFailure(new NullPointerException("Continuation returned null"));
                }else {
                    pExtensionTask.addOnSuccessListener(ExtensionReviserTask.this);
                    pExtensionTask.addOnFailureListener(ExtensionReviserTask.this);
                }
            }
        });
    }

    @Override
    public void OnFailure(Exception exception) {
        pExtension.setException(exception);
    }

    @Override
    public void OnSuccess(PExtension pExtension) {
        ExtensionReviserTask.this.pExtension.setResult(pExtension);
    }
}
