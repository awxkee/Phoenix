package com.github.dozzatq.phoenix.Tasks;

import android.support.annotation.NonNull;

/**
 * Created by dxfb on 10.06.2017.
 */

interface ControlQueueService<PState> {
    public void shout(@NonNull ControllableTask<PState> pResultTask);
    public boolean maybeRemove(Object criteria);
}
