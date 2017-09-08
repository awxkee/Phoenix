package com.github.dozzatq.phoenix.advertising;

import com.github.dozzatq.phoenix.tasks.Task;

/**
 * Created by Rodion Bartoshik on 04.07.2017.
 */

abstract class InterstitialTrace<T> extends InterstitialHelper implements StateTracer{

    private InterstitialHelper traceHelper;

    final static int STATE_BURNED = 1;
    final static int STATE_LOADED = 2;
    final static int STATE_FAILED = 3;

    protected abstract int getConfig();

    private Task<T> stateTask = new Task<T>();

    InterstitialTrace(InterstitialHelper traceHelper) {
        this.traceHelper = traceHelper;
    }

    @Override
    public void OnInterstitialShowed(FactoryAd factoryAd) {
        StatisticsInterstitialDelegate.getInstance().OnInterstitialShowed(factoryAd, getConfig());
        traceHelper.OnInterstitialShowed(factoryAd);
    }

    @Override
    public void OnInterstitialDismissed(FactoryAd factoryAd) {
        StatisticsInterstitialDelegate.getInstance().OnInterstitialDismissed(factoryAd, getConfig());
        traceHelper.OnInterstitialDismissed(factoryAd);
    }

    @Override
    public void OnInterstitialImpression(FactoryAd factoryAd) {
        StatisticsInterstitialDelegate.getInstance().OnInterstitialImpression(factoryAd, getConfig());
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
