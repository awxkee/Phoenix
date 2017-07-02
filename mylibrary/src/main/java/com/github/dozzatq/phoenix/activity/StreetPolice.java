package com.github.dozzatq.phoenix.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;

/**
 * Created by Rodion Bartoshyk on 01.07.2017.
 */

public abstract class StreetPolice{
    private final Object mLock = new Object();

    @MainThread
    public void onCreate(Bundle bundle) {
    }

    @MainThread
    public void onStart() {
    }

    @MainThread
    public void onResume() {
    }

    @MainThread
    public void onSaveInstanceState(Bundle bundle) {
    }

    @MainThread
    public void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
    }

    @MainThread
    public void onStop() {
    }

    @MainThread
    public void onDestroy() {
    }

    private boolean destroyed=false;
    private boolean stopped=false;

    public final void destroy()
    {
        synchronized (mLock)
        {
            destroyed=true;
        }
    }

    public final void stop()
    {
        synchronized (mLock)
        {
            stopped = true;
        }
    }

    public final void start()
    {
        synchronized (mLock) {
            stopped = false;
        }
    }

    public final boolean isStopped()
    {
        synchronized (mLock)
        {
            return stopped;
        }
    }

    public final boolean isDestroyed()
    {
        synchronized (mLock)
        {
            return destroyed;
        }
    }

}
