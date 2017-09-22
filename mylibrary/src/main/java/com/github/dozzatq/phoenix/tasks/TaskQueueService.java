package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

/**
 * Created by Rodion Bartoshik on 04.06.2017.
 */

interface TaskQueueService<PResult> {
    void sync(@NonNull Task<PResult> pResultTask);
    boolean maybeRemove(Object criteria);
    boolean needSync(@NonNull Task<PResult> pResultTask);
    boolean isKeepSynced();
}
