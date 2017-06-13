package com.github.dozzatq.phoenix.Tasks56;

import android.support.annotation.NonNull;

/**
 * Created by dxfb on 27.05.2017.
 */

public interface Extension<PResult, PExtension> {
    PExtension then(@NonNull Task<PResult> task) throws Exception;
}
