package com.github.dozzatq.phoenix.tasks;

import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshik on 31.05.2017.
 */

class UnionTask<PFirst, PNext> extends TaskUnion implements OnSuccessListener<Void>{

    private Task<PFirst> pFirstTask;
    private Task<PNext> pNextTask;
    private OnUnionListener<PFirst, PNext> unionListener;
    private Executor executor;

    UnionTask(Executor executor, Task<PFirst> pFirstTask, Task<PNext> pNextTask,
              OnUnionListener<PFirst, PNext> unionListener)
    {
        super(pFirstTask, pFirstTask);
        this.pFirstTask = pFirstTask;
        this.pNextTask = pNextTask;
        this.executor = executor;
        this.unionListener = unionListener;
        addOnSuccessListener(this.executor, this, false);
    }

    @Override
    public void OnSuccess(Void aVoid) {
        if (unionListener!=null)
            unionListener.when(pFirstTask, pNextTask);
    }
}
