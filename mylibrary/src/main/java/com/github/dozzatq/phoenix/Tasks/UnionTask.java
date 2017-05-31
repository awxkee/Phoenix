package com.github.dozzatq.phoenix.Tasks;

import android.os.Handler;

/**
 * Created by dxfb on 31.05.2017.
 */

class UnionTask<PFirst, PNext> implements OnCompleteListener, OnFailureListener  {

    private Task<PFirst> pFirstTask;
    private Task<PNext> pNextTask;
    private Handler handler;
    private final Object waitObject = new Object();
    private OnUnionListener<PFirst, PNext> nextOnUnionListener;

    public UnionTask(Task<PFirst> pFirstTask, Task<PNext> pNextTask, OnUnionListener<PFirst, PNext> nextOnUnionListener)
    {
        synchronized (waitObject) {
            handler = new Handler();
            this.pFirstTask = pFirstTask;
            this.pNextTask = pNextTask;
            this.nextOnUnionListener = nextOnUnionListener;
            pFirstTask.addOnCompleteListener(this);
            pNextTask.addOnCompleteListener(this);
            pFirstTask.addOnFailureListener(this);
            pNextTask.addOnFailureListener(this);
        }
    }

    @Override
    public void OnFailure(Exception exception) {
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
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        nextOnUnionListener.when(pFirstTask, pNextTask);
                    }
                });
        }
    }
}
