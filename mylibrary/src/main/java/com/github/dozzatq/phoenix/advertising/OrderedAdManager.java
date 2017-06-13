package com.github.dozzatq.phoenix.advertising;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dxfb on 03.05.2017.
 */

public class OrderedAdManager extends FactoryAd{

    private List<FactoryAd> factoryAdsList;

    private List<InterstitialHelper> interstitialHelpersOrder;
    private List<OrderAdState> interstitialFailedId;

    private List<NativeHelper> nativeHelperOrder;
    private List<OrderAdState> nativeHelperFailedId;

    private FactoryAd catchedOrderNativeAd;

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
        loadInterstitial(config, new InterstitialHelper() {
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
        interstitialFailedId.clear();
        interstitialHelpersOrder.clear();
        for (int iOrder = 0; iOrder < factoryAdsList.size(); iOrder++) {
            final OrderAdState adState = new OrderAdState();

            final InterstitialHelper helper = new InterstitialHelper() {
                @Override
                public void OnInterstitialShowed(FactoryAd factoryAd) {
                    interstitialHelper.OnInterstitialShowed(factoryAd);
                }

                @Override
                public void OnInterstitialLoaded(FactoryAd factoryAd) {
                    adState.setLoaded(true);
                    prepareOrderIntestitialLoaded(interstitialHelper);
                }

                @Override
                public void OnInterstitialFailedToLoad(FactoryAd factoryAd) {
                    adState.setFailed(true);
                    prepareOrderIntestitialLoaded(interstitialHelper);
                }

                @Override
                public void OnInterstitialDismissed(FactoryAd factoryAd) {
                    interstitialHelper.OnInterstitialDismissed(factoryAd);
                }

                @Override
                public void OnInterstitialImpression(FactoryAd factoryAd) {
                    interstitialHelper.OnInterstitialImpression(factoryAd);
                }
            };
            interstitialFailedId.add(adState);
            interstitialHelpersOrder.add(helper);
            FactoryAd factoryAd = factoryAdsList.get(iOrder);
            factoryAd.setNote(iOrder);
            factoryAd.loadInterstitial(config, helper);
        }
    }

    private void prepareOrderIntestitialLoaded(InterstitialHelper helper)
    {
        for (OrderAdState orderAdState : interstitialFailedId) {
            if (orderAdState.isCalled())
                return;
        }

        for (int i = 0; i < interstitialHelpersOrder.size(); i++) {

            FactoryAd factoryAdFirst = factoryAdsList.get(i);
            int prefBefore = i - 1;
            if (prefBefore < 0)
                prefBefore = 0;
            FactoryAd factoryAdBefore = factoryAdsList.get(prefBefore);

            if (interstitialFailedId.get(prefBefore).isCalled())
            {
                break;
            }

            if (!interstitialFailedId.get(i).isCalled()
                    && !interstitialFailedId.get(i).isLoaded() && !interstitialFailedId.get(i).isFailed()) {
                return;
            }

            if (!interstitialFailedId.get(prefBefore).isFailed())
            {
                if (factoryAdBefore.isInterstitialLoaded())
                {
                    interstitialFailedId.get(prefBefore).setCalled(true);
                    helper.OnInterstitialLoaded(factoryAdBefore);
                    break;
                }
            }
            if (factoryAdFirst.isInterstitialLoaded())
            {
                interstitialFailedId.get(i).setCalled(true);
                helper.OnInterstitialLoaded(factoryAdFirst);
                break;
            }
            else if (interstitialFailedId.get(i).isFailed())
            {
                int next = i + 1 ;
                if (next == interstitialHelpersOrder.size())
                    next = i;
                FactoryAd factoryAdNext = factoryAdsList.get(next);
                if (factoryAdNext.isInterstitialLoaded())
                {
                    interstitialFailedId.get(next).setCalled(true);
                    helper.OnInterstitialLoaded(factoryAdNext);
                    break;
                }
            }
        }
    }

    @Override
    public void loadNative(int config, final NativeHelper nativeHelper) {
        nativeHelperFailedId.clear();
        nativeHelperOrder.clear();
        catchedOrderNativeAd = null;
        for (int iOrder = 0; iOrder < factoryAdsList.size(); iOrder++) {
            final OrderAdState adState = new OrderAdState();

            final NativeHelper helper = new NativeHelper() {
                @Override
                public void OnNativeLoaded(FactoryAd factoryAd) {
                    adState.setLoaded(true);
                    prepareOrderNativeLoaded(nativeHelper);
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
                    adState.setFailed(true);
                    prepareOrderNativeLoaded(nativeHelper);
                }
            };

            nativeHelperFailedId.add(adState);
            nativeHelperOrder.add(helper);
            FactoryAd factoryAd = factoryAdsList.get(iOrder);
            factoryAd.setNote(iOrder);
            factoryAd.loadNative(config, helper);
        }
    }

    private void prepareOrderNativeLoaded(NativeHelper helper)
    {
        for (OrderAdState orderAdState : nativeHelperFailedId) {
            if (orderAdState.isCalled())
                return;
        }

        for (int i = 0; i < nativeHelperOrder.size(); i++) {

            FactoryAd factoryAdFirst = factoryAdsList.get(i);
            int prefBefore = i - 1;
            if (prefBefore < 0)
                prefBefore = 0;
            FactoryAd factoryAdBefore = factoryAdsList.get(prefBefore);

            if (nativeHelperFailedId.get(prefBefore).isCalled())
            {
                break;
            }

            if (!nativeHelperFailedId.get(i).isCalled()
                    && !nativeHelperFailedId.get(i).isLoaded() && !nativeHelperFailedId.get(i).isFailed()) {
                return;
            }

            if (!nativeHelperFailedId.get(prefBefore).isFailed())
            {
                if (factoryAdBefore.isNativeAdLoaded())
                {
                    nativeHelperFailedId.get(prefBefore).setCalled(true);
                    catchedOrderNativeAd = factoryAdBefore;
                    helper.OnNativeLoaded(factoryAdBefore);
                    break;
                }
            }

            if (nativeHelperFailedId.get(i).isCalled())
            {
                break;
            }
            if (factoryAdFirst.isNativeAdLoaded())
            {
                nativeHelperFailedId.get(i).setCalled(true);
                catchedOrderNativeAd = factoryAdFirst;
                helper.OnNativeLoaded(factoryAdFirst);
                break;
            }
            else if (nativeHelperFailedId.get(i).isFailed())
            {
                int next = i + 1 ;
                if (next == nativeHelperOrder.size())
                    next = i;
                FactoryAd factoryAdNext = factoryAdsList.get(next);
                if (factoryAdNext.isNativeAdLoaded())
                {
                    nativeHelperFailedId.get(next).setCalled(true);
                    catchedOrderNativeAd = factoryAdNext;
                    helper.OnNativeLoaded(factoryAdNext);
                    break;
                }
            }
        }
    }

    @Override
    public View returnNativeView() {
        return null;
    }

    public FactoryAd getOrderNative()
    {
        return catchedOrderNativeAd;
    }

    @Override
    public void bindView(View view, int bindMethod) {
        if (catchedOrderNativeAd!=null)
            catchedOrderNativeAd.bindView(view, bindMethod);
    }

    @Override
    public void bindHolder(RecyclerView.ViewHolder viewHolder) {
        if (catchedOrderNativeAd!=null)
            catchedOrderNativeAd.bindHolder(viewHolder);
    }

    @Override
    public boolean isInterstitialLoaded() {
        return false;
    }

    @Override
    public String isInstance() {
        return "ordered";
    }

    @Override
    public boolean isNativeAdLoaded() {
        return catchedOrderNativeAd != null && catchedOrderNativeAd.isNativeAdLoaded();
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
        interstitialHelpersOrder = new ArrayList<>();
        interstitialFailedId = new ArrayList<>();
        nativeHelperOrder = new ArrayList<>();
        nativeHelperFailedId = new ArrayList<>();
        return this;
    }

    private class OrderAdState{
        private boolean loaded;
        private boolean failed;
        private boolean called;

        public OrderAdState()
        {
            loaded = false;
            failed = false;
            called = false;
        }

        public boolean isFailed() {
            return failed;
        }

        public void setFailed(boolean failed) {
            this.failed = failed;
        }

        public boolean isLoaded() {
            return loaded;
        }

        public void setLoaded(boolean loaded) {
            this.loaded = loaded;
        }

        public boolean isCalled() {
            return called;
        }

        public void setCalled(boolean called) {
            this.called = called;
        }
    }
}
