package com.github.dozzatq.phoenix.Tasks;

import android.support.annotation.NonNull;

/**
 * Created by dxfb on 04.06.2017.
 */

interface OnTaskCompleteListener<PResult> {
    public void OnTaskComplete(@NonNull Task<PResult> pResultTask);
}
