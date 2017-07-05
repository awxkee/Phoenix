package com.github.dozzatq.phoenix.activity;

/**
 * Created by Rodion Bartoshyk on 30.06.2017.
 */

public class CallbackSupplier<T> {

    private StreetPolice streetPolice;

    protected final Object mLock = new Object();

    public boolean equals(Object object)
    {
        return this.callback.equals(object);
    }

    private T callback;

    public CallbackSupplier(T object, StreetPolice streetPolice)
    {
        this.callback = object;
        this.streetPolice = streetPolice;
        if (streetPolice==null)
                    this.streetPolice = new StreetPolice() {
                @Override
                public void onDestroy() {
                    destroy();
                }
            };
    }

    public final boolean isStopped()
    {
        synchronized (mLock) {
            return streetPolice.isStopped();
        }
    }
    public final boolean isDestroyed()
    {
        synchronized (mLock) {
            return streetPolice.isDestroyed();
        }
    }

    public T get()
    {
        synchronized (mLock) {
            return callback;
        }
    }

    public StreetPolice getStreetPolice() {
        synchronized (mLock) {
            return streetPolice;
        }
    }
}
