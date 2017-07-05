package com.github.dozzatq.phoenix.advertising;

/**
 * Created by dxfb on 05.07.2017.
 */

public abstract class LoyalityWrapper<NA, VH> {
    private NA nativeAd;
    private VH viewHolder;

    public LoyalityWrapper(VH viewHolder) {
        this.viewHolder = viewHolder;
    }

    public void setNativeAd(NA nativeAd) {
        this.nativeAd = nativeAd;
    }

    abstract void wrap();
}
