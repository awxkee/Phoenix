package com.github.dozzatq.phoenix.advertising;

import com.github.dozzatq.phoenix.tasks.OnSuccessListener;
import com.github.dozzatq.phoenix.tasks.Task;

import java.util.List;

/**
 * Created by Rodion Bartoshik on 05.07.2017.
 */

class NativeBridge extends NativeReviser implements OnSuccessListener<FactoryAd>{

    private OnSuccessListener<FactoryAd> factoryAdOnSuccessListener;

    NativeBridge(List<FactoryAd> factoryAdList, NativeHelper nativeHelper, OnSuccessListener<FactoryAd> factoryAdOnSuccessListener) {
        super(factoryAdList, nativeHelper);
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
