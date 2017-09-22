package com.github.dozzatq.phoenix.user;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.github.dozzatq.phoenix.util.PhoenixDeviceIdGenerator;

import java.util.Locale;

/**
 * Created by Rodion Bartoshik on 12.09.2017.
 */

public class LightSession {

    private Context mContext;
    private PackageManager mManager;
    private PackageInfo mInfo;

    public LightSession(Context context) {
        this.mContext = context;
        this.mManager = context.getPackageManager();
        try {
            mInfo = mManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException("This package not found !");
        }
    }

    public String getUserIdentifier()
    {
        return PhoenixDeviceIdGenerator.readDeviceId(mContext);
    }

    private Locale getCurrentLocale(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return mContext.getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return mContext.getResources().getConfiguration().locale;
        }
    }

    public String getLanguage()
    {
        return getCurrentLocale().getLanguage();
    }

    public String getCountry()
    {
        return getCurrentLocale().getCountry();
    }

    public String getLocaleName()
    {
        return getCurrentLocale().toString();
    }

    public String getDeviceModel()
    {
        return Build.MODEL;
    }

    public int getVersionCode()
    {
        return mInfo.versionCode;
    }

    public String getPackageName()
    {
        return mContext.getPackageName();
    }

    public String getVersionName()
    {
        return mInfo.versionName;
    }
}
