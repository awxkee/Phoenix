package com.github.dozzatq.phoenix.advertising;

/**
 * Created by Rodion Bartoshik on 04.07.2017.
 */

interface StateTracer {
    void traceLoaded(FactoryAd factoryAd);
    void traceFailedToLoad(FactoryAd factoryAd);
}
