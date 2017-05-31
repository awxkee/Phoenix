package com.github.dozzatq.phoenix.Tasks;

/**
 * Created by dxfb on 31.05.2017.
 */

public interface OnUnionListener<PFirst, PNext> {
    public void when(Task<PFirst> pFirstTask, Task<PNext> pNextTask);
}
