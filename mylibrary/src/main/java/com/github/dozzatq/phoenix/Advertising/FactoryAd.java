package com.github.dozzatq.phoenix.Advertising;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by dxfb on 03.05.2017.
 */

public abstract class FactoryAd extends AdCreator {
    public abstract void showInterstitial(int config, InterstitialHelper interstitialHelper);
    public abstract void loadInterstitial(int config, InterstitialHelper interstitialHelper);
    public abstract void loadNative(int config, NativeHelper nativeHelper);
    public abstract View returnNativeView();
    public abstract void bindView(View view, int bindMethod);
    public abstract void bindHolder(RecyclerView.ViewHolder viewHolder);
    public abstract boolean isInterstitialLoaded();
    public abstract String isInstance();
    public abstract boolean isNativeAdLoaded();
    public abstract void setNote(Object object);
    public abstract Object getNote();
    public abstract String placementForConfig(int config);
}
