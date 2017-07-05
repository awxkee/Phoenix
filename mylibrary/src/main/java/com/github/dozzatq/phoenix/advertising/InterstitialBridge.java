package com.github.dozzatq.phoenix.advertising;

import com.github.dozzatq.phoenix.tasks.OnSuccessListener;

import java.util.List;

/**
 * Created by dxfb on 05.07.2017.
 */

class InterstitialBridge extends InterstitialReviser implements OnSuccessListener<FactoryAd> {

    private OnSuccessListener<FactoryAd> factoryAdOnSuccessListener;

    InterstitialBridge(List<FactoryAd> factoryAdList, InterstitialHelper interstitialHelper, OnSuccessListener<FactoryAd> factoryAdOnSuccessListener) {
        super(factoryAdList, interstitialHelper);
        this.factoryAdOnSuccessListener = factoryAdOnSuccessListener;
    }

    @Override
    public void OnSuccess(FactoryAd factoryAd) {
        factoryAdOnSuccessListener.OnSuccess(factoryAd);
        snap().traceLoaded(factoryAd);
    }
}
