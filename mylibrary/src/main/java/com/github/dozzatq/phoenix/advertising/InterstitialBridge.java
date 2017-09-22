package com.github.dozzatq.phoenix.advertising;

import com.github.dozzatq.phoenix.tasks.OnSuccessListener;
import com.github.dozzatq.phoenix.tasks.Task;

import java.util.List;

/**
 * Created by Rodion Bartoshik on 05.07.2017.
 */

class InterstitialBridge extends InterstitialReviser implements OnSuccessListener<FactoryAd> {

    private OnSuccessListener<FactoryAd> factoryAdOnSuccessListener;

    InterstitialBridge(List<FactoryAd> factoryAdList, InterstitialHelper interstitialHelper,
                       OnSuccessListener<FactoryAd> factoryAdOnSuccessListener) {
        super(factoryAdList, interstitialHelper);
        this.factoryAdOnSuccessListener = factoryAdOnSuccessListener;
    }

    @Override
    public Task<FactoryAd> promise(int config) {
        return super.promise(config).addOnSuccessListener(this);
    }

    @Override
    public void OnSuccess(FactoryAd factoryAd) {
        factoryAdOnSuccessListener.OnSuccess(factoryAd);
        snap().traceLoaded(factoryAd);
    }
}
