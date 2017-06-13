package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

/**
 * Created by dxfb on 04.06.2017.
 */

interface TaskQueueService<PResult> {
    public void done(@NonNull Task<PResult> pResultTask);
    public boolean maybeRemove(Object criteria);
}
