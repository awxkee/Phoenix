package com.github.dozzatq.phoenix.Tasks56;

import android.support.annotation.NonNull;

/**
 * Created by dxfb on 31.05.2017.
 */

public interface OnUnionListener<PFirst, PNext> {
    public void when(@NonNull Task<PFirst> pFirstTask, @NonNull Task<PNext> pNextTask);
}
