package com.github.dozzatq.phoenix.advertising;

/**
 * Created by Rodion Bartoshyk on 04.07.2017.
 */

interface StateTracer {
    void traceLoaded(FactoryAd factoryAd);
    void traceFailedToLoad(FactoryAd factoryAd);
}
