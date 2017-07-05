package com.github.dozzatq.phoenix.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by Rodion Bartoshyk on 27.06.2017.
 */

public class ActivityConnector extends Fragment implements ActivitySupplierInterface{
    private static WeakHashMap<Activity, WeakReference<ActivityConnector>> activityWeakReferenceWeakHashMap = new WeakHashMap<>();
    private Map<String, ActivitySupplier> lifecycleCallbacks = new ArrayMap<>();
    private int activityState;
    private Bundle bundle;

    public ActivityConnector()
    {}

    public static ActivityConnector create(Activity activity)
    {
        ActivityConnector activityConnector;
        WeakReference<ActivityConnector> connectorWeakReference;
        if ( ( connectorWeakReference= activityWeakReferenceWeakHashMap.get(activity) )!=null && (activityConnector = connectorWeakReference.get())!=null )
            return activityConnector;
        else {
            try {
                activityConnector = (ActivityConnector)activity.getFragmentManager().findFragmentByTag("PhoenixQueueLifecycleFragmentImpl");
            } catch (ClassCastException var4) {
                throw new IllegalStateException("Fragment with tag PhoenixQueueLifecycleFragmentImpl is not a PhoenixQueueLifecycleFragmentImpl", var4);
            }

            if (activityConnector==null || activityConnector.isRemoving())
            {
                activityConnector = new ActivityConnector();
                activity.getFragmentManager().beginTransaction().add(activityConnector, "PhoenixQueueLifecycleFragmentImpl").commitAllowingStateLoss();
            }
            activityWeakReferenceWeakHashMap.put(activity, new WeakReference<ActivityConnector>(activityConnector));
            return activityConnector;
        }
    }

    @Override
    public <T extends ActivitySupplier> T tryGetSupplier(String key, Class<T> callbackClass) {
        return callbackClass.cast(lifecycleCallbacks.get(key));
    }

    @Override
    public final void addListenerInterface(String key, ActivitySupplier activitySupplier)
    {
        if (!lifecycleCallbacks.containsKey(key)){
            lifecycleCallbacks.put(key, activitySupplier);
            if (activityState>0)
            {
                new Handler(Looper.getMainLooper()).post(new ActivityInformator(this,activityState, activitySupplier, key));
            }
        }else {
            throw new IllegalStateException("This Lifecycle Callback already added to fragment");
        }
    }

    Bundle getBundle() {
        return bundle;
    }


    @Override
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.activityState = 1;
        this.bundle = bundle;

        for (Map.Entry<String, ActivitySupplier> entry : this.lifecycleCallbacks.entrySet()) {
            Map.Entry entry1;
            ((ActivitySupplier) (entry1 = (Map.Entry) entry).getValue())
                    .onCreate(bundle != null ? bundle.getBundle((String) entry1.getKey()) : null);
        }
    }

    @Override
    public final void onStart() {
        super.onStart();
        this.activityState = 2;

        for (ActivitySupplier supplier : this.lifecycleCallbacks.values()) {
            supplier.onStart();
        }

    }

    @Override
    public final void onResume() {
        super.onResume();
        this.activityState = 3;

        for (ActivitySupplier supplier : this.lifecycleCallbacks.values()) {
            supplier.onResume();
        }

    }

    @Override
    public final void onActivityResult(int var1, int var2, Intent var3) {
        super.onActivityResult(var1, var2, var3);

        for (ActivitySupplier supplier : this.lifecycleCallbacks.values()) {
            supplier.onActivityResult(var1, var2, var3);
        }
    }

    @Override
    public final void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if(bundle != null) {

            for (Object o : this.lifecycleCallbacks.entrySet()) {
                Map.Entry var3 = (Map.Entry) o;
                Bundle savedCallbacks = new Bundle();
                ((ActivitySupplier) var3.getValue()).onSaveInstanceState(savedCallbacks);
                bundle.putBundle((String) var3.getKey(), savedCallbacks);
            }

        }
    }

    @Override
    public final void onStop() {
        super.onStop();
        this.activityState = 4;

        for (ActivitySupplier supplier :this.lifecycleCallbacks.values()) {
            supplier.onStop();
        }

    }

    @Override
    public final void onDestroy() {
        super.onDestroy();
        this.activityState = 5;

        for (ActivitySupplier supplier : this.lifecycleCallbacks.values()) {
            ((ActivitySupplier) supplier).onDestroy();
        }

    }
}
