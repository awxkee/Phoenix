package com.github.dozzatq.phoenix.Advertising67;

/**
 * Created by dxfb on 03.05.2017.
 */

public abstract class NativeHelper {

    public abstract void OnNativeLoaded(FactoryAd factoryAd);
    public abstract void OnNativeBind(FactoryAd factoryAd);
    public abstract void OnImpressionNative(FactoryAd factoryAd);
    public abstract void OnNativeFailedToLoad(FactoryAd factoryAd);
}
