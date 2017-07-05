package com.github.dozzatq.phoenix.advertising;

import com.github.dozzatq.phoenix.tasks.Task;

/**
 * Created by Rodion Bartoshyk on 04.07.2017.
 */

abstract class NativeTrace<T> extends NativeHelper implements StateTracer{

    private NativeHelper traceHelper;

    final static int STATE_BURNED = 1;
    final static int STATE_LOADED = 2;
    final static int STATE_FAILED = 3;

    private Task<T> stateTask = new Task<T>();

    NativeTrace(NativeHelper traceHelper) {
        this.traceHelper = traceHelper;
    }

    @Override
    public void OnNativeBind(FactoryAd factoryAd) {
        traceHelper.OnNativeBind(factoryAd);
    }

    @Override
    public void OnImpressionNative(FactoryAd factoryAd) {
        traceHelper.OnImpressionNative(factoryAd);
    }

    @Override
    public void traceLoaded(FactoryAd factoryAd)
    {
        traceHelper.OnNativeLoaded(factoryAd);
    }

    @Override
    public void traceFailedToLoad(FactoryAd factoryAd)
    {
        traceHelper.OnNativeFailedToLoad(factoryAd);
    }

    Task<T> getTask() {
        return stateTask;
    }
}
