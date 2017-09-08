package com.github.dozzatq.phoenix.advertising;


import android.support.annotation.NonNull;

/**
 * Created by Rodion Bartoshik on 23.08.2017.
 */

public class StatisticsInterstitialDelegate extends TraceInterstitialHelper{

    private static StatisticsInterstitialDelegate localInstance = null;

    private StatisticsInterstitialDelegate() {

    }

    public static StatisticsInterstitialDelegate newInstance(TraceInterstitialHelper helper) {
        StatisticsInterstitialDelegate localInstance = StatisticsInterstitialDelegate.localInstance;
        if (localInstance == null) {
            synchronized (StatisticsInterstitialDelegate.class) {
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
            synchronized (StatisticsInterstitialDelegate.class) {
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

    @Override
    public void OnInterstitialShowed(FactoryAd factoryAd, int config) {
        if (interstitialHelper!=null)
            interstitialHelper.OnInterstitialShowed(factoryAd, config);
    }

    @Override
    public void OnInterstitialLoaded(FactoryAd factoryAd, int config) {
        if (interstitialHelper!=null)
            interstitialHelper.OnInterstitialLoaded(factoryAd, config);
    }

    @Override
    public void OnInterstitialFailedToLoad(FactoryAd factoryAd, int config) {
        if (interstitialHelper!=null)
            interstitialHelper.OnInterstitialFailedToLoad(factoryAd, config);
    }

    @Override
    public void OnInterstitialDismissed(FactoryAd factoryAd, int config) {
        if (interstitialHelper!=null)
            interstitialHelper.OnInterstitialDismissed(factoryAd, config);
    }

    @Override
    public void OnInterstitialImpression(FactoryAd factoryAd, int config) {
        if (interstitialHelper!=null)
            interstitialHelper.OnInterstitialImpression(factoryAd, config);
    }

    @Override
    public void OnRequest(@NonNull FactoryAd factoryAd, int config) {
        if (interstitialHelper!=null)
            interstitialHelper.OnRequest(factoryAd, config);
    }
}
