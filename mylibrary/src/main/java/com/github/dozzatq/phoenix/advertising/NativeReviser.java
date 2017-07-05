package com.github.dozzatq.phoenix.advertising;

import android.support.annotation.NonNull;

import com.github.dozzatq.phoenix.tasks.OnTaskSuccessListener;
import com.github.dozzatq.phoenix.tasks.Task;
import com.github.dozzatq.phoenix.tasks.Tasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Rodion Bartoshyk on 04.07.2017.
 */

class NativeReviser implements Prominence<FactoryAd>, OnTaskSuccessListener<Void>, Snapper<NativeBundle>{

    private List<FactoryAd> factoryAdList;
    private NativeHelper nativeHelper;
    private final List<Task<NativeBundle>> promiseTasks = new ArrayList<>();
    private Task<FactoryAd> factoryAdTask = new Task<>();

    private boolean called;
    private boolean burnedOut = false;
    private boolean isColded = false;

    NativeReviser(List<FactoryAd> factoryAdList, NativeHelper nativeHelper) {
        this.nativeHelper = nativeHelper;
        if (factoryAdList==null)
            throw new NullPointerException("Factory ad list must not be null !");
        called = false;
        isColded = true;
        this.factoryAdList = factoryAdList;
    }

    @Override
    public Task<FactoryAd> promise(int config) {

        synchronized (promiseTasks) {
            if (called)
                throw new IllegalStateException("Native Reviser already called !!!!");
            called = true;

            for (FactoryAd ad : factoryAdList) {
                NativeBundle nativeBundle = new NativeBundle(ad, nativeHelper);
                promiseTasks.add(nativeBundle.promise(config));
            }

            Tasks.whenAll(factoryAdTask).addOnTaskSuccessListener(this);

            return factoryAdTask;
        }
    }

    @Override
    public void cold() {
        synchronized (promiseTasks) {
            isColded = true;
        }
    }

    @Override
    public void OnTaskSuccess(@NonNull Task<Void> voidTask) {
        synchronized (promiseTasks) {
            if (isColded)
                return;
            List<Task<NativeBundle>> listSuccessTasks = new ArrayList<>();
            List<Task<NativeBundle>> listFailureTasks = new ArrayList<>();
            Iterator<Task<NativeBundle>> taskIterator = promiseTasks.iterator();
            Task<NativeBundle> bundleTask;

            while (taskIterator.hasNext() && (bundleTask = taskIterator.next()) != null && bundleTask.getResult().state() == NativeTrace.STATE_LOADED)
                listSuccessTasks.add(bundleTask);

            taskIterator = promiseTasks.iterator();

            while (taskIterator.hasNext() && (bundleTask = taskIterator.next()) != null && bundleTask.getResult().state() == NativeTrace.STATE_FAILED)
                listFailureTasks.add(bundleTask);

            taskIterator = listFailureTasks.iterator();

            while (taskIterator.hasNext() && (bundleTask = taskIterator.next()) != null)
                bundleTask.getResult().traceFailedToLoad(bundleTask.getResult().reflection());

            if (listSuccessTasks.size() == 0)
                return;
            burnedOut = true;
            NativeBundle successBundle = listSuccessTasks.get(0).getResult();
            bundle = successBundle;
            factoryAdTask.setResult(successBundle.reflection());
        }
    }

    private NativeBundle bundle;

    public boolean isBurnedOut() {
        synchronized (promiseTasks) {
            return !isColded && burnedOut;
        }
    }

    @Override
    public NativeBundle snap() {
        synchronized (promiseTasks) {
            return null;
        }
    }
}
