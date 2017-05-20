package com.github.dozzatq.phoenix.Advertising;

/**
 * Created by dxfb on 03.05.2017.
 */

public abstract class InterstitialHelper {
    public abstract void OnInterstitialShowed(FactoryAd factoryAd);
    public abstract void OnInterstitialLoaded(FactoryAd factoryAd);
    public abstract void OnInterstitialFailedToLoad(FactoryAd factoryAd);
    public abstract void OnInterstitialDismissed(FactoryAd factoryAd);
    public abstract void OnInterstitialImpression(FactoryAd factoryAd);
}
