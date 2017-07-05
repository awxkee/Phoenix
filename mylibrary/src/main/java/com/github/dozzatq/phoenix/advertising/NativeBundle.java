package com.github.dozzatq.phoenix.advertising;

import com.github.dozzatq.phoenix.tasks.Task;

/**
 * Created by Rodion Bartoshyk on 04.07.2017.
 */

class NativeBundle extends NativeTrace<NativeBundle> implements Prominence<NativeBundle>, Reflector {

    private FactoryAd factoryAd;
    private boolean called;

    NativeBundle(FactoryAd factoryAd,NativeHelper traceHelper) {
        super(traceHelper);
        this.factoryAd = factoryAd;
        called = false;
        if (factoryAd==null)
            throw new NullPointerException("Factory ad must not be null !");
    }

    @Override
    public Task<NativeBundle> promise(int config) {

        if (called)
            throw new IllegalStateException("This Native Bundle Already Configured !!!!");

        called = true;
        state = STATE_BURNED;
        factoryAd.loadNative(config, this);
        return getTask();
    }

    @Override
    public void cold() {

    }

    @Override
    public FactoryAd reflection() {
        return factoryAd;
    }

    private int state=-1;

    @Override
    public int state() {
        return state;
    }

    @Override
    public void OnNativeLoaded(FactoryAd factoryAd) {
        state = STATE_LOADED;
        getTask().setResult(this);
    }

    @Override
    public void OnNativeFailedToLoad(FactoryAd factoryAd) {
        state = STATE_FAILED;
        getTask().setResult(this);
    }
}
