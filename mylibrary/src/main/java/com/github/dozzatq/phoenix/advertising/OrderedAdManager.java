package com.github.dozzatq.phoenix.advertising;

import com.github.dozzatq.phoenix.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Rodion Bartoshyk on 03.05.2017.
 */

public class OrderedAdManager extends FactoryAd{

    private List<FactoryAd> factoryAdsList;
    private FactoryAd preferredNativeAd;

    public OrderedAdManager addOrderedFactory(FactoryAd... factoryAds)
    {
        if (factoryAds==null)
            return this;
        factoryAdsList.addAll(Arrays.asList(factoryAds));
        return this;
    }

    public OrderedAdManager addOrder(FactoryAd factoryAd)
    {
        if (factoryAd==null)
            return this;
        factoryAdsList.add(factoryAd);
        return this;
    }

    @Override
    public void showInterstitial(final int config, final InterstitialHelper interstitialHelper) {
        if (preferredInterstitial!=null)
            preferredInterstitial.showInterstitial(config, interstitialHelper);
        else loadInterstitial(config, new InterstitialHelper() {
            @Override
            public void OnInterstitialShowed(FactoryAd factoryAd) {
                interstitialHelper.OnInterstitialShowed(factoryAd);
            }

            @Override
            public void OnInterstitialLoaded(FactoryAd factoryAd) {
                interstitialHelper.OnInterstitialLoaded(factoryAd);
                factoryAd.showInterstitial(config, interstitialHelper);
            }

            @Override
            public void OnInterstitialFailedToLoad(FactoryAd factoryAd) {
                interstitialHelper.OnInterstitialFailedToLoad(factoryAd);
            }

            @Override
            public void OnInterstitialDismissed(FactoryAd factoryAd) {
                interstitialHelper.OnInterstitialDismissed(factoryAd);
            }

            @Override
            public void OnInterstitialImpression(FactoryAd factoryAd) {
                interstitialHelper.OnInterstitialImpression(factoryAd);
            }
        });
    }

    @Override
    public void loadInterstitial(final int config, final InterstitialHelper interstitialHelper) {
        synchronized (mLock) {
            preferredInterstitial = null;
            if (interstitialBridge !=null)
                interstitialBridge.cold();
            interstitialBridge = new InterstitialBridge(factoryAdsList, interstitialHelper, new OnSuccessListener<FactoryAd>() {
                @Override
                public void OnSuccess(FactoryAd factoryAd) {
                    synchronized (mLock) {
                        preferredInterstitial = factoryAd;
                    }
                }
            });
            interstitialBridge.promise(config);
        }
    }

    private NativeBridge nativeBridge;
    private InterstitialBridge interstitialBridge;
    private FactoryAd preferredInterstitial;

    private final Object mLock = new Object();

    @Override
    public void loadNative(int config, final NativeHelper nativeHelper) {
        synchronized (mLock) {
            preferredNativeAd = null;
            if (nativeBridge !=null)
                nativeBridge.cold();
            nativeBridge = new NativeBridge(factoryAdsList, nativeHelper, new OnSuccessListener<FactoryAd>() {
                @Override
                public void OnSuccess(FactoryAd factoryAd) {
                    synchronized (mLock) {
                        preferredNativeAd = factoryAd;
                    }
                }
            });
            nativeBridge.promise(config);
        }
    }

    public FactoryAd getOrderNative()
    {
        return preferredNativeAd;
    }

    @Override
    public boolean isInterstitialLoaded() {
        return interstitialBridge != null && interstitialBridge.isBurnedOut();
    }

    @Override
    public String isInstance() {
        return "ordered";
    }

    @Override
    public boolean isNativeAdLoaded() {
        return nativeBridge != null && nativeBridge.isBurnedOut();
    }

    @Override
    public LoyalityWrapper wrap(LoyalityWrapper wrapper) {
        if (preferredNativeAd!=null)
            return preferredNativeAd.wrap(wrapper);
        return wrapper;
    }

    @Override
    public void setNote(Object object) {

    }

    @Override
    public Object getNote() {
        return null;
    }

    @Override
    public String placementForConfig(int config) {
        return null;
    }

    @Override
    public FactoryAd createAd() {
        factoryAdsList = new ArrayList<>();
        return this;
    }

}
