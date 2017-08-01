package com.github.dozzatq.phoenix.advertising;

import com.github.dozzatq.phoenix.tasks.Task;

/**
 * Created by Rodion Bartoshyk on 04.07.2017.
 */

abstract class InterstitialTrace<T> extends InterstitialHelper implements StateTracer{

    private InterstitialHelper traceHelper;

    final static int STATE_BURNED = 1;
    final static int STATE_LOADED = 2;
    final static int STATE_FAILED = 3;

    private Task<T> stateTask = new Task<T>();

    InterstitialTrace(InterstitialHelper traceHelper) {
        this.traceHelper = traceHelper;
    }

    @Override
    public void OnInterstitialShowed(FactoryAd factoryAd) {
        traceHelper.OnInterstitialShowed(factoryAd);
    }

    @Override
    public void OnInterstitialDismissed(FactoryAd factoryAd) {
        traceHelper.OnInterstitialDismissed(factoryAd);
    }

    @Override
    public void OnInterstitialImpression(FactoryAd factoryAd) {
        traceHelper.OnInterstitialImpression(factoryAd);
    }

    @Override
    public void traceLoaded(FactoryAd factoryAd)
    {
        traceHelper.OnInterstitialLoaded(factoryAd);
    }

    @Override
    public void traceFailedToLoad(FactoryAd factoryAd)
    {
        traceHelper.OnInterstitialFailedToLoad(factoryAd);
    }

    Task<T> getTask() {
        return stateTask;
    }
}
