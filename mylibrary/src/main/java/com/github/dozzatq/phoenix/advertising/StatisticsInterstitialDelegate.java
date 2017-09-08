package com.github.dozzatq.phoenix.advertising;

import com.github.dozzatq.phoenix.Phoenix;

/**
 * Created by Rodion Bartoshik on 23.08.2017.
 */

public class StatisticsInterstitialDelegate {

    private static StatisticsInterstitialDelegate localInstance = null;

    private StatisticsInterstitialDelegate() {

    }

    public static StatisticsInterstitialDelegate newInstance(TraceInterstitialHelper helper) {
        StatisticsInterstitialDelegate localInstance = StatisticsInterstitialDelegate.localInstance;
        if (localInstance == null) {
            synchronized (Phoenix.class) {
                localInstance = StatisticsInterstitialDelegate.localInstance;
                if (localInstance == null) {
                    StatisticsInterstitialDelegate.localInstance = localInstance = new StatisticsInterstitialDelegate(helper);
                }
            }
        }
        return localInstance;
    }

    public static StatisticsInterstitialDelegate getInstance() {
        StatisticsInterstitialDelegate localInstance = StatisticsInterstitialDelegate.localInstance;
        if (localInstance == null) {
            synchronized (Phoenix.class) {
                localInstance = StatisticsInterstitialDelegate.localInstance;
                if (localInstance == null) {
                    StatisticsInterstitialDelegate.localInstance = localInstance = new StatisticsInterstitialDelegate();
                }
            }
        }
        return localInstance;
    }

    private TraceInterstitialHelper interstitialHelper;

    private StatisticsInterstitialDelegate(TraceInterstitialHelper interstitialHelper) {
        this.interstitialHelper = interstitialHelper;
    }

    void OnInterstitialShowed(FactoryAd factoryAd, int config) {
        if (interstitialHelper!=null)
            interstitialHelper.OnInterstitialShowed(factoryAd, config);
    }

    void OnInterstitialLoaded(FactoryAd factoryAd, int config) {
        if (interstitialHelper!=null)
            interstitialHelper.OnInterstitialLoaded(factoryAd, config);
    }

    void OnInterstitialFailedToLoad(FactoryAd factoryAd, int config) {
        if (interstitialHelper!=null)
            interstitialHelper.OnInterstitialFailedToLoad(factoryAd, config);
    }

    void OnInterstitialDismissed(FactoryAd factoryAd, int config) {
        if (interstitialHelper!=null)
            interstitialHelper.OnInterstitialDismissed(factoryAd, config);
    }

    void OnInterstitialImpression(FactoryAd factoryAd, int config) {
        if (interstitialHelper!=null)
            interstitialHelper.OnInterstitialImpression(factoryAd, config);
    }
}
