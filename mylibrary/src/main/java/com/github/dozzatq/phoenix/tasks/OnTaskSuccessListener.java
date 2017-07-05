package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

/**
 * Created by Rodion Bartoshyk on 23.06.2017.
 */

public interface OnTaskSuccessListener<P> {
    public void OnTaskSuccess(@NonNull Task<P> pTask);
}
