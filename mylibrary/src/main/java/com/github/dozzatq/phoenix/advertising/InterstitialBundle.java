package com.github.dozzatq.phoenix.advertising;


import com.github.dozzatq.phoenix.tasks.Task;


/**
 * Created by Rodion Bartoshyk on 04.07.2017.
 */

class InterstitialBundle extends InterstitialTrace<InterstitialBundle> implements Prominence<InterstitialBundle>, Reflector {

    private FactoryAd factoryAd;

    private boolean called;
    private int state;

    InterstitialBundle(FactoryAd factoryAd, InterstitialHelper traceHelper) {
        super(traceHelper);
        this.factoryAd = factoryAd;
    }

    @Override
    public Task<InterstitialBundle> promise(int config) {
        if (called)
            throw new IllegalStateException("This Native Bundle Already Configured !!!!");

        called = true;
        state = STATE_BURNED;
        factoryAd.loadInterstitial(config, this);
        return getTask();
    }

    @Override
    public void cold() {

    }

    @Override
    public void OnInterstitialLoaded(FactoryAd factoryAd) {
        state = STATE_LOADED;
        getTask().setResult(this);
    }

    @Override
    public void OnInterstitialFailedToLoad(FactoryAd factoryAd) {
        state = STATE_FAILED;
        getTask().setResult(this);
    }

    @Override
    public FactoryAd reflection() {
        return factoryAd;
    }

    @Override
    public int state() {
        return state;
    }
}
