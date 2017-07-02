package com.github.dozzatq.phoenix.activity;
/**
 * Created by Rodion Bartoshyk on 27.06.2017.
 */

interface ActivitySupplierInterface {
    void addListenerInterface(String key, ActivitySupplier activitySupplier);
    <T extends ActivitySupplier> T tryGetSupplier(String key, Class<T> callbackClass);
}
