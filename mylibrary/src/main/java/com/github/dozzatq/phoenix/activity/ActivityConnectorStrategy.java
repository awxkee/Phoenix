package com.github.dozzatq.phoenix.activity;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Rodion Bartoshik on 27.06.2017.
 */

public class ActivityConnectorStrategy implements ActivitySupplierInterface{
    private Activity activity;
    private ActivitySupplierInterface supplierInterface;

    private ActivityConnectorStrategy(Activity activity) {
        if (activity==null)
            throw new NullPointerException("Activity must not be null");
        this.activity = activity;
    }

    public static ActivityConnectorStrategy connect(Activity activity)
    {
        ActivityConnectorStrategy strategy = new ActivityConnectorStrategy(activity);
        if (strategy.isSupportActivity())
            strategy.setSupplierInterface(ActivitySupportConnector.create((FragmentActivity) activity));
        else
            strategy.setSupplierInterface(ActivityConnector.create(activity));
        return strategy;
    }

    public boolean isSupportActivity()
    {
        return activity instanceof FragmentActivity;
    }

    public Activity getActivity()
    {
        return activity;
    }

    public FragmentActivity getFragmentActivity()
    {
        return (FragmentActivity) activity;
    }

    public ActivitySupplierInterface getSupplierInterface() {
        return supplierInterface;
    }

    private void setSupplierInterface(ActivitySupplierInterface supplierInterface) {
        this.supplierInterface = supplierInterface;
    }

    @Override
    public void addListenerInterface(String key, ActivitySupplier activitySupplier) {
        supplierInterface.addListenerInterface(key, activitySupplier);
    }

    @Override
    public <T extends ActivitySupplier> T tryGetSupplier(String key, Class<T> callbackClass) {
        return supplierInterface.tryGetSupplier(key, callbackClass);
    }
}
