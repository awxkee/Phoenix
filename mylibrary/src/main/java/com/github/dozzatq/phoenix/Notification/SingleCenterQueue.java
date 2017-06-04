package com.github.dozzatq.phoenix.Notification;

import java.util.concurrent.Executor;

/**
 * Created by dxfb on 04.06.2017.
 */

class SingleCenterQueue extends DefaultCenterQueue {

    public SingleCenterQueue(Executor queueExecutor) {
        super(queueExecutor);
        keepSynced = false;
    }
}
