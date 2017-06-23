package com.github.dozzatq.phoenix.tasks;

import android.support.annotation.NonNull;

/**
 * Created by dxfb on 23.06.2017.
 */

public interface OnTaskFailureListener<P> {
    public void OnTaskException(@NonNull Task<P> pTask);
}
