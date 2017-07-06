package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

/**
 * Created by Rodion Bartoshyk on 04.06.2017.
 */

interface TaskQueueService<PResult> {
    public void sync(@NonNull Task<PResult> pResultTask);
    public boolean maybeRemove(Object criteria);
    public boolean needSync(@NonNull Task<PResult> pResultTask);
    public boolean isKeepSynced();
}
