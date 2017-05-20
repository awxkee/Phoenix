package com.github.dozzatq.phoenix;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.AnyThread;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.RawRes;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.github.dozzatq.phoenix.Core.PhoenixCore;
import com.github.dozzatq.phoenix.Fonts.PhoenixTypeface;
import com.github.dozzatq.phoenix.Notification.PhoenixCenter;
import com.github.dozzatq.phoenix.Notification.PhoenixNotification;
import com.github.dozzatq.phoenix.Prefs.PhoenixPreferences;
import com.github.dozzatq.phoenix.Util.AndroidUtilities;

import java.io.File;
import java.util.Locale;

/**
 * Created by Rodion on 05.12.2016.
 */

public class Phoenix {

    private static Phoenix ourInstance = null;

    public static Phoenix getInstance() {
        Phoenix localInstance = ourInstance;
        if (localInstance == null) {
            synchronized (Phoenix.class) {
                localInstance = ourInstance;
                if (localInstance == null) {
                    ourInstance = localInstance = new Phoenix();
                }
            }
        }
        return localInstance;
    }

    private Context applicationContext;

    public PhoenixCenter getCenter()
    {
        return PhoenixCenter.getInstance();
    }

    public PhoenixCore getCore() {
        return PhoenixCore.getInstance();
    }

    public void init(Context applicationContext)
    {
        ourInstance.setContext(applicationContext);
    }

    public String getString(@StringRes int resId)
    {
        return getContext().getString(resId);
    }

    public Drawable getDrawable(@DrawableRes int resId)
    {
        return ContextCompat.getDrawable(getContext(), resId);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public int checkSelfPermission(String permission)
    {
        return getContext().checkSelfPermission(permission);
    }

    public PackageManager getPackageManager()
    {
        return getContext().getPackageManager();
    }

    public Resources getResources()
    {
        return getContext().getResources();
    }

    public Context getApplicationContext()
    {
        return getContext().getApplicationContext();
    }

    public File getCacheDir()
    {
        return getContext().getCacheDir();
    }

    public File getExternalCacheDir(){
        return getContext().getExternalCacheDir();
    }

    public Looper getMainLooper()
    {
        return getContext().getMainLooper();
    }

    public Phoenix startService(Intent intentService)
    {
        getContext().startService(intentService);
        return this;
    }

    public Phoenix stopService(Intent intentService)
    {
        getContext().stopService(intentService);
        return this;
    }

    public Typeface getTypeface(String assetPath)
    {
        return PhoenixTypeface.getTypeface(assetPath);
    }

    @AnyThread
    public PhoenixCenter addListener(String notificationKey, PhoenixNotification phoenixNotification)
    {
        return getCenter().addListener(notificationKey, phoenixNotification);
    }

    @AnyThread
    public PhoenixCenter addListenerForSingleEvent(String notificationKey, PhoenixNotification phoenixNotification)
    {
        return getCenter().addListenerForSingleEvent(notificationKey, phoenixNotification);
    }

    @AnyThread
    public PhoenixCenter removeSingleEventListener(String notificationKey, PhoenixNotification phoenixNotification)
    {
        return getCenter().removeSingleEventListener(notificationKey, phoenixNotification);
    }

    @AnyThread
    public PhoenixCenter removeListener(String notificationKey, PhoenixNotification phoenixNotification)
    {
        return getCenter().removeListener(notificationKey, phoenixNotification);
    }

    @AnyThread
    public PhoenixCenter removeAllSingleEventListeners(String notificationKey)
    {
        return getCenter().removeAllSingleEventListeners(notificationKey);
    }

    @AnyThread
    public PhoenixCenter removeAllListeners(String notificationKey)
    {
        return getCenter().removeAllListeners(notificationKey);
    }

    @AnyThread
    public PhoenixCenter postNotificationDelayed(final String notificationKey, int delay, final Object... values)
    {
        getCenter().postNotificationDelayed(notificationKey, delay, values);
        return getCenter();
    }

    @AnyThread
    public PhoenixCenter postNotification(final String notificationKey,final Object... values)
    {
        getCenter().postNotification(notificationKey, values);
        return getCenter();
    }

    @AnyThread
    public PhoenixCenter postNotificationSingleEventListeners(final String notificationKey, final Object... values){
        getCenter().postNotificationSingleEventListeners(notificationKey, values);
        return getCenter();
    }

    public int getColor(@ColorRes int resId)
    {
        return ContextCompat.getColor(getContext(), resId);
    }

    public File getExternalFilesDir(String res)
    {
        return getContext().getExternalFilesDir(res);
    }

    public Phoenix startActivity(Intent shallStart)
    {
        ActivityCompat.startActivity(getContext(), shallStart, null);
        return this;
    }

    public PhoenixPreferences getPreferences()
    {
        return PhoenixPreferences.getInstance();
    }

    public AssetManager getAssets()
    {
        return getContext().getAssets();
    }

    public String getPackageName()
    {
        return getContext().getPackageName();
    }

    public Object getSystemService(String service)
    {
        return getContext().getSystemService(service);
    }

    public String[] getStringArray(@ArrayRes int resId)
    {
        return getContext().getResources().getStringArray(resId);
    }

    public Context getContext() {
        return applicationContext;
    }

    public void setContext(Context applicationContext) {
        this.applicationContext = applicationContext;
        new AndroidUtilities();
    }

    public Phoenix putString(String key, String value)
    {
        PhoenixPreferences.getInstance().putString(key, value);
        return this;
    }

    public boolean putStringFuture(String key, String value)
    {
        return PhoenixPreferences.getInstance().putStringFuture(key, value);
    }

    public String getString(String key)
    {
        return PhoenixPreferences.getInstance().getString(key, null);
    }

    public Phoenix putBoolean(String key, boolean value)
    {
        PhoenixPreferences.getInstance().putBoolean(key, value);
        return this;
    }

    public boolean putBooleanFuture(String key, boolean value)
    {
        return PhoenixPreferences.getInstance().putBooleanFuture(key, value);
    }

    public boolean getBoolean(String key, boolean default_bool)
    {
        return PhoenixPreferences.getInstance().getBoolean(key, default_bool);
    }

    public Phoenix removeValue(String key)
    {
        PhoenixPreferences.getInstance().removeValue(key);
        return this;
    }

    public Phoenix putLong(String key, Long value)
    {
        PhoenixPreferences.getInstance().putLongFuture(key, value);
        return this;
    }

    public boolean putLongFuture(String key, Long value)
    {
        return PhoenixPreferences.getInstance().putLongFuture(key, value);
    }

    public Phoenix runOnUIThread(Runnable runnable, int delay)
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, delay);
        return this;
    }

    public Phoenix runOnUIThread(Runnable runnable)
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
        return this;
    }

    public Uri getRawPath(@RawRes int resId)
    {
        return Uri.parse(String.format(Locale.US,"android.resource://%s/", getPackageName()) + resId);
    }

    public long getLong(String key, long defValue) {
        return PhoenixPreferences.getInstance().getLong(key, defValue);
    }

    public Phoenix putFloat(String key, Float value)
    {
        PhoenixPreferences.getInstance().putFloat(key, value);
        return this;
    }

    public boolean putFloatFuture(String key, Float value)
    {
        return PhoenixPreferences.getInstance().putFloatFuture(key, value);
    }

    public float getFloat(String key, float defValue) {
        return PhoenixPreferences.getInstance().getFloat(key, defValue);
    }

    public Phoenix putInt(String key, Integer value)
    {
        PhoenixPreferences.getInstance().putInt(key, value);
        return this;
    }

    public boolean putIntFuture(String key, Integer value)
    {
        return PhoenixPreferences.getInstance().putIntFuture(key, value);
    }

    public int getInt(String key, int defValue) {
        return PhoenixPreferences.getInstance().getInt(key, defValue);
    }

    public String getString(String key, String defValue) {
        return PhoenixPreferences.getInstance().getString(key, defValue);
    }

}
