package com.github.dozzatq.phoenix.advertising;

import android.support.annotation.NonNull;

/**
 * Created by Rodion Bartoshik on 23.08.2017.
 */

public abstract class TraceInterstitialHelper {
    public abstract void OnInterstitialShowed(@NonNull FactoryAd factoryAd, int config);
    public abstract void OnInterstitialLoaded(@NonNull FactoryAd factoryAd, int config);
    public abstract void OnInterstitialFailedToLoad(@NonNull FactoryAd factoryAd, int config);
    public abstract void OnInterstitialDismissed(@NonNull FactoryAd factoryAd, int config);
    public abstract void OnInterstitialImpression(@NonNull FactoryAd factoryAd, int config);
    public abstract void OnRequest(@NonNull FactoryAd factoryAd, int config);
}
