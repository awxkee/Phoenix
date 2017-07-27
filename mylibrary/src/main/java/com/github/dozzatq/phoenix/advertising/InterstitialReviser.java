package com.github.dozzatq.phoenix.advertising;

import android.support.annotation.NonNull;

import com.github.dozzatq.phoenix.tasks.OnTaskSuccessListener;
import com.github.dozzatq.phoenix.tasks.Task;
import com.github.dozzatq.phoenix.tasks.Tasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dxfb on 04.07.2017.
 */

class InterstitialReviser implements Prominence<FactoryAd>, OnTaskSuccessListener<Void>, Snapper<InterstitialBundle> {

    private List<FactoryAd> factoryAdList;
    private InterstitialHelper interstitialHelper;
    private final List<Task<InterstitialBundle>> promiseTasks = new ArrayList<>();
    private Task<FactoryAd> factoryAdTask = new Task<>();

    private boolean called;
    private boolean burnedOut = false;
    private boolean isColded = false;

    InterstitialReviser(List<FactoryAd> factoryAdList, InterstitialHelper interstitialHelper) {
        this.interstitialHelper = interstitialHelper;
        if (factoryAdList==null)
            throw new NullPointerException("Factory ad list must not be null !");
        called = false;
        isColded = false;
        this.factoryAdList = factoryAdList;
    }

    @Override
    public Task<FactoryAd> promise(int config) {

        synchronized (promiseTasks) {
            if (called)
                throw new IllegalStateException("Native Reviser already called !!!!");
            called = true;

            for (FactoryAd ad : factoryAdList) {
                InterstitialBundle InterstitialBundle = new InterstitialBundle(ad, interstitialHelper);
                promiseTasks.add(InterstitialBundle.promise(config));
            }

            Tasks.whenAll(promiseTasks).addOnTaskSuccessListener(this);

            return factoryAdTask;
        }
    }

    @Override
    public void cold() {
        synchronized (promiseTasks) {
            isColded = true;
        }
    }

    private InterstitialBundle bundle;

    @Override
    public InterstitialBundle snap()
    {
        synchronized (promiseTasks) {
            return bundle;
        }
    }

    @Override
    public void OnTaskSuccess(@NonNull Task<Void> voidTask) {
        synchronized (promiseTasks) {
            if (isColded)
                return;
            List<Task<InterstitialBundle>> listSuccessTasks = new ArrayList<>();
            List<Task<InterstitialBundle>> listFailureTasks = new ArrayList<>();
            Iterator<Task<InterstitialBundle>> taskIterator = promiseTasks.iterator();
            Task<InterstitialBundle> bundleTask;

            while (taskIterator.hasNext() && (bundleTask = taskIterator.next()) != null)
                if (bundleTask.getResult().state() == InterstitialTrace.STATE_LOADED)
                    listSuccessTasks.add(bundleTask);

            taskIterator = promiseTasks.iterator();

            while (taskIterator.hasNext() && (bundleTask = taskIterator.next()) != null )
                if (bundleTask.getResult().state() == InterstitialTrace.STATE_FAILED)
                    listFailureTasks.add(bundleTask);

            taskIterator = listFailureTasks.iterator();

            while (taskIterator.hasNext() && (bundleTask = taskIterator.next()) != null)
                bundleTask.getResult().traceFailedToLoad(bundleTask.getResult().reflection());

            if (listSuccessTasks.size() == 0)
                return;
            burnedOut = true;
            InterstitialBundle successBundle = listSuccessTasks.get(0).getResult();
            bundle = successBundle;
            factoryAdTask.setResult(successBundle.reflection());
        }
    }

    public boolean isBurnedOut() {
        synchronized (promiseTasks) {
            return !isColded && burnedOut;
        }
    }

}
