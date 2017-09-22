package com.github.dozzatq.phoenix.advertising;

import android.support.annotation.NonNull;

/**
 * Created by Rodion Bartoshik on 9/8/17.
 */

public class StatisticsNativeDelegate extends TraceNativeHelper {
    private static StatisticsNativeDelegate localInstance = null;

    private StatisticsNativeDelegate(TraceNativeHelper nativeHelper) {
        mNativeHelper = nativeHelper;
    }

    private TraceNativeHelper mNativeHelper;

    private StatisticsNativeDelegate() {

    }

    public static StatisticsNativeDelegate newInstance(TraceNativeHelper helper) {
        StatisticsNativeDelegate localInstance = StatisticsNativeDelegate.localInstance;
        if (localInstance == null) {
            synchronized (StatisticsNativeDelegate.class) {
                localInstance = StatisticsNativeDelegate.localInstance;
                if (localInstance == null) {
                    StatisticsNativeDelegate.localInstance = localInstance = new StatisticsNativeDelegate(helper);
                }
            }
        }
        return localInstance;
    }

    public static StatisticsNativeDelegate getInstance() {
        StatisticsNativeDelegate localInstance = StatisticsNativeDelegate.localInstance;
        if (localInstance == null) {
            synchronized (StatisticsNativeDelegate.class) {
                localInstance = StatisticsNativeDelegate.localInstance;
                if (localInstance == null) {
                    StatisticsNativeDelegate.localInstance = localInstance = new StatisticsNativeDelegate();
                }
            }
        }
        return localInstance;
    }

    @Override
    public void OnNativeLoaded(@NonNull FactoryAd factoryAd, int config) {
        if (mNativeHelper != null)
            mNativeHelper.OnNativeLoaded(factoryAd, config);
    }

    @Override
    public void OnNativeBind(@NonNull FactoryAd factoryAd, int config) {
        if (mNativeHelper != null)
            mNativeHelper.OnNativeBind(factoryAd, config);
    }

    @Override
    public void OnImpressionNative(@NonNull FactoryAd factoryAd, int config) {
        if (mNativeHelper != null)
            mNativeHelper.OnImpressionNative(factoryAd, config);
    }

    @Override
    public void OnNativeFailedToLoad(@NonNull FactoryAd factoryAd, int config) {
        if (mNativeHelper != null)
            mNativeHelper.OnNativeFailedToLoad(factoryAd, config);
    }

    @Override
    public void OnRequest(@NonNull FactoryAd factoryAd, int config) {
        if (mNativeHelper != null)
            mNativeHelper.OnRequest(factoryAd, config);
    }
}
