package com.github.dozzatq.phoenix.advertising;

/**
 * Created by Rodion Bartoshyk on 03.05.2017.
 */

public abstract class InterstitialHelper {
    public abstract void OnInterstitialShowed(FactoryAd factoryAd);
    public abstract void OnInterstitialLoaded(FactoryAd factoryAd);
    public abstract void OnInterstitialFailedToLoad(FactoryAd factoryAd);
    public abstract void OnInterstitialDismissed(FactoryAd factoryAd);
    public abstract void OnInterstitialImpression(FactoryAd factoryAd);
}
