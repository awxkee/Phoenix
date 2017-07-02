package com.github.dozzatq.phoenix.activity;

/**
 * Created by Rodion Bartoshyk on 27.06.2017.
 */

class ActivitySupportInformator implements Runnable {
    private ActivitySupportConnector activitySupportConnector;
    private int activityState;
    private ActivitySupplier activitySupplier;
    private String key;

    ActivitySupportInformator(ActivitySupportConnector activitySupportConnector, int activityState, ActivitySupplier activitySupplier, String key) {
        this.activitySupportConnector = activitySupportConnector;
        this.activityState = activityState;
        this.activitySupplier = activitySupplier;
        this.key = key;
    }

    @Override
    public void run() {
        if(activityState>0) {
            activitySupplier.onCreate(activitySupportConnector.getBundle() != null?activitySupportConnector.getBundle().getBundle(key):null);
        }

        if(activityState >= 2) {
            activitySupplier.onStart();
        }

        if(activityState >= 3) {
            activitySupplier.onResume();
        }

        if(activityState >= 4) {
            activitySupplier.onStop();
        }

        if(activityState >= 5) {
            activitySupplier.onDestroy();
        }
    }
}
