package com.github.dozzatq.phoenix.Tasks56;

import android.support.annotation.NonNull;

/**
 * Created by dxfb on 10.06.2017.
 */

interface StateQueueService<PState> {
    public void shout(@NonNull CancellableTask<PState> pResultTask);
    public boolean maybeRemove(Object criteria);
}
