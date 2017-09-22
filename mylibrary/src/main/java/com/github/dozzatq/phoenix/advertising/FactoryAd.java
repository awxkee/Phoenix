package com.github.dozzatq.phoenix.advertising;

/**
 * Created by Rodion Bartoshik on 03.05.2017.
 */

public abstract class FactoryAd<NA> extends AdCreator {

    private NA snapNativeAd;

    public abstract void showInterstitial(int config, InterstitialHelper interstitialHelper);
    public abstract void loadInterstitial(int config, InterstitialHelper interstitialHelper);
    public abstract void loadNative(int config, NativeHelper nativeHelper);
    public abstract boolean isInterstitialLoaded();
    public abstract String isInstance();
    public abstract boolean isNativeAdLoaded();

    public void setSnap(NA nativeAd)
    {
        snapNativeAd = nativeAd;
    }

    public NA getSnap()
    {
        return snapNativeAd;
    }

    public <VH> LoyalityWrapper<NA,VH> wrap(LoyalityWrapper<NA, VH> wrapper)
    {
        wrapper.setNativeAd(snapNativeAd);
        wrapper.wrap();
        return wrapper;
    }

    public abstract void setNote(Object object);
    public abstract Object getNote();
    public abstract String placementForConfig(int config);
}
