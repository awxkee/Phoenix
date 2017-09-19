package com.github.dozzatq.phoenix.advertising;

import android.support.annotation.NonNull;

/**
 * Created by Rodion Bartoshyk on 9/8/17.
 */

public abstract class TraceNativeHelper {
    public abstract void OnNativeLoaded(@NonNull FactoryAd factoryAd, int config);
    public abstract void OnNativeBind(@NonNull FactoryAd factoryAd, int config);
    public abstract void OnImpressionNative(@NonNull FactoryAd factoryAd, int config);
    public abstract void OnNativeFailedToLoad(@NonNull FactoryAd factoryAd, int config);
    public abstract void OnRequest(@NonNull FactoryAd factoryAd, int config);
}
