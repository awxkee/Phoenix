package com.github.dozzatq.phoenix.advertising;

import com.github.dozzatq.phoenix.tasks.Task;

/**
 * Created by Rodion Bartoshik on 04.07.2017.
 */

interface Prominence<T> {
    Task<T> promise(int config);
    void cold();
}
