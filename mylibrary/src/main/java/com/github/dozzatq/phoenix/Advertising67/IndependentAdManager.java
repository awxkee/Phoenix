package com.github.dozzatq.phoenix.Advertising67;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dxfb on 01.06.2017.
 */

public class IndependentAdManager extends FactoryAd {

    private List<FactoryAd> queueList;

    public IndependentAdManager()
    {
        queueList = new ArrayList<>();
    }

    @Deprecated
    public void showInterstitial(int i, InterstitialHelper interstitialHelper) {

    }

    @Deprecated
    public void loadInterstitial(int i, InterstitialHelper interstitialHelper) {

    }

    public IndependentAdManager pushToQueue(FactoryAd factoryAd)
    {
        queueList.add(factoryAd);
        return this;
    }

    @Override
    public void loadNative(int i, final NativeHelper nativeHelper) {
        for (FactoryAd ad : queueList) {
            ad.loadNative(i, new NativeHelper() {
                @Override
                public void OnNativeLoaded(FactoryAd factoryAd) {
                    nativeHelper.OnNativeLoaded(factoryAd);
                }

                @Override
                public void OnNativeBind(FactoryAd factoryAd) {
                    nativeHelper.OnNativeBind(factoryAd);
                }

                @Override
                public void OnImpressionNative(FactoryAd factoryAd) {
                    nativeHelper.OnImpressionNative(factoryAd);
                }

                @Override
                public void OnNativeFailedToLoad(FactoryAd factoryAd) {
                    nativeHelper.OnNativeFailedToLoad(factoryAd);
                }
            });
        }
    }

    @Deprecated
    public View returnNativeView() {
        return null;
    }

    @Override
    public void bindView(View view, int i) {
        for (FactoryAd factoryAd : queueList) {
            if (factoryAd.isNativeAdLoaded())
                factoryAd.bindView(view, i);
        }
    }

    @Override
    public void bindHolder(RecyclerView.ViewHolder viewHolder) {
        for (FactoryAd factoryAd : queueList) {
            if (factoryAd.isNativeAdLoaded())
                factoryAd.bindHolder(viewHolder);
        }
    }

    @Deprecated
    public boolean isInterstitialLoaded() {
        return false;
    }

    @Override
    public String isInstance() {
        if (getFirstNativeLoaded()!=null)
            return getFirstNativeLoaded().isInstance();
        else
            return "";
    }

    @Override
    public boolean isNativeAdLoaded() {
        return getFirstNativeLoaded() != null && getFirstNativeLoaded().isNativeAdLoaded();
    }

    private Object object;

    @Override
    public void setNote(Object o) {
        object = o;
    }

    public FactoryAd getFirstNativeLoaded()
    {
        for (FactoryAd factoryAd : queueList) {
            if (factoryAd.isNativeAdLoaded())
                return factoryAd;
        }
        return null;
    }

    @Override
    public Object getNote() {
        return object;
    }

    @Deprecated
    public String placementForConfig(int i) {
        return null;
    }

    @Override
    public FactoryAd createAd() {
        return this;
    }
}
