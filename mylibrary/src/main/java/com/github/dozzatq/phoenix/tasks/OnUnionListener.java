package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

/**
 * Created by Rodion Bartoshik on 31.05.2017.
 */

public interface OnUnionListener<PFirst, PNext> {
    public void when(@NonNull Task<PFirst> pFirstTask, @NonNull Task<PNext> pNextTask);
}
