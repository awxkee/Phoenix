package com.github.dozzatq.phoenix.advertising;

/**
 * Created by Rodion Bartoshik on 23.08.2017.
 */

public abstract class TraceInterstitialHelper {
    public abstract void OnInterstitialShowed(FactoryAd factoryAd, int config);
    public abstract void OnInterstitialLoaded(FactoryAd factoryAd, int config);
    public abstract void OnInterstitialFailedToLoad(FactoryAd factoryAd, int config);
    public abstract void OnInterstitialDismissed(FactoryAd factoryAd, int config);
    public abstract void OnInterstitialImpression(FactoryAd factoryAd, int config);
}
