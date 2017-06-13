package com.github.dozzatq.phoenix.Tasks56;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by dxfb on 31.05.2017.
 */

class UnionTask<PFirst, PNext> implements OnCompleteListener, OnFailureListener  {

    private Task<PFirst> pFirstTask;
    private Task<PNext> pNextTask;
    private final Object waitObject = new Object();
    private OnUnionListener<PFirst, PNext> nextOnUnionListener;
    private Executor executor;

    public UnionTask(Executor executor, Task<PFirst> pFirstTask, Task<PNext> pNextTask, OnUnionListener<PFirst, PNext> nextOnUnionListener)
    {
        synchronized (waitObject) {
            this.pFirstTask = pFirstTask;
            this.pNextTask = pNextTask;
            this.executor = executor;
            this.nextOnUnionListener = nextOnUnionListener;
            pFirstTask.addOnCompleteListener(this);
            pNextTask.addOnCompleteListener(this);
            pFirstTask.addOnFailureListener(this);
            pNextTask.addOnFailureListener(this);
        }
    }

    @Override
    public void OnFailure(@NonNull Exception exception) {
        synchronized (waitObject) {
            if (pFirstTask == null || pNextTask == null)
                return;
        }
    }

    @Override
    public void OnComplete(Object o) {
        synchronized (waitObject) {
            if (pFirstTask == null || pNextTask == null)
                return;
            if (pFirstTask.isComplete() && pNextTask.isComplete())
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        nextOnUnionListener.when(pFirstTask, pNextTask);
                    }
                });
        }
    }
}
