package com.github.dozzatq.phoenix.Tasks;

import java.util.concurrent.Executor;

/**
 * Created by dxfb on 28.05.2017.
 */

public class ExtensionReviser<PResult, PExtension> implements OnExtensionListener<PResult> {

    private Executor executor;
    private Extension<PResult, PExtension> extensionTask;
    private Task<PExtension> pExtension;

    public ExtensionReviser(Executor executor, Extension<PResult, PExtension> extensionTask, Task<PExtension> pExtension) {
        this.executor = executor;
        this.extensionTask = extensionTask;
        this.pExtension = pExtension;
    }

    @Override
    public void OnExtension(final Task<PResult> pResult) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                PExtension pExtensionTask = null;
                try {
                    pExtensionTask = extensionTask.then(pResult);
                } catch (Exception e) {
                    pExtension.setException(e);
                }

                pExtension.setResult(pExtensionTask);
            }
        });
    }

}
