package com.github.dozzatq.phoenix.notification;

import java.util.concurrent.Executor;

/**
 * Created by Rodion Bartoshyk on 04.06.2017.
 */

class CenterQueue extends DefaultCenterQueue {

    public CenterQueue(Executor queueExecutor) {
        super(queueExecutor);
    }
}
