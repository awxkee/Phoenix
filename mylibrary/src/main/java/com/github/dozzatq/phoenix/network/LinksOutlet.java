package com.github.dozzatq.phoenix.network;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.dozzatq.phoenix.Phoenix;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

/**
 * Created by Rodion Bartoshyk on 13.09.2017.
 */

public class LinksOutlet {

    public static final String META_DOMAIN = "com.phoenix.dynamic.domain";
    public static final String META_IOS_STORE_APPID = "com.phoenix.dynamic.ios.store.appid";
    public static final String META_IOS_BUNDLE = "com.phoenix.dynamic.ios.bundle";
    public static final String META_ANDROID_BUNDLE = "com.phoenix.dynamic.android.bundle";
    public static final String META_ANDROID_MIN_VERSION = "com.phoenix.dynamic.android.min.version";

    private static String mAppStoreId=null;
    private static String mIosBundle=null;
    private static String mAppDomain=null;
    private static String mAndroidPackage = null;
    private static String mAndroidMinVersion = null;

    public static Uri getLink(@NonNull Uri url)
    {
        return getNativeLink(url, null, null,null, null, null, null).buildDynamicLink().getUri();
    }

    public static Uri getLink(@NonNull Uri url, @Nullable String title, @Nullable String description, @Nullable Uri imageUri)
    {
        return getNativeLink(url, title, description, imageUri, null, null,null).buildDynamicLink().getUri();
    }

    public static Uri getLink(@NonNull Uri url, @Nullable String campaign, @Nullable String source, @Nullable String medium)
    {
        return getNativeLink(url, null, null, null, campaign, source,medium).buildDynamicLink().getUri();
    }

    public static Uri getLink(@NonNull Uri url, @Nullable String title, @Nullable String description, @Nullable Uri imageUri,
                                               @Nullable String campaign, @Nullable String source, @Nullable String medium)
    {
        return getNativeLink(url, title, description, imageUri, campaign, source, medium).buildDynamicLink().getUri();
    }

    public static Task<ShortDynamicLink> getShortenLink(@NonNull Uri url)
    {
        return getNativeLink(url, null, null,null, null, null, null).buildShortDynamicLink();
    }

    public static Task<ShortDynamicLink> getShortenLink(@NonNull Uri url, @Nullable String title, @Nullable String description, @Nullable Uri imageUri)
    {
        return getNativeLink(url, title, description, imageUri, null, null,null).buildShortDynamicLink();
    }

    public static Task<ShortDynamicLink> getShortenLink(@NonNull Uri url, @Nullable String campaign, @Nullable String source, @Nullable String medium)
    {
        return getNativeLink(url, null, null, null, campaign, source,medium).buildShortDynamicLink();
    }

    public static Task<ShortDynamicLink> getShortenLink(@NonNull Uri url, @Nullable String title, @Nullable String description, @Nullable Uri imageUri,
                                              @Nullable String campaign, @Nullable String source, @Nullable String medium)
    {
        return getNativeLink(url, title, description, imageUri, campaign, source, medium).buildShortDynamicLink();
    }

    private static DynamicLink.Builder getNativeLink(@NonNull Uri url, @Nullable String title, @Nullable String description, @Nullable Uri imageUri,
        @Nullable String campaign, @Nullable String source, @Nullable String medium)
    {
        if (mAppDomain==null || mIosBundle == null || mAppStoreId == null) {
            try {
                ApplicationInfo ai;
                ai = Phoenix.getInstance().getPackageManager()
                        .getApplicationInfo(Phoenix.getInstance().getPackageName(),
                                PackageManager.GET_META_DATA);
                Bundle bundle = ai.metaData;
                mAppDomain = bundle.getString(META_DOMAIN);
                mIosBundle = bundle.getString(META_IOS_BUNDLE);
                mAppStoreId = String.valueOf(bundle.getInt(META_IOS_STORE_APPID));
                mAndroidPackage = bundle.getString(META_ANDROID_BUNDLE);
                mAndroidMinVersion = String.valueOf(bundle.getInt(META_ANDROID_MIN_VERSION));
            } catch (PackageManager.NameNotFoundException ignored) {
            } catch (NullPointerException ignored) {
            }
        }

        if (mAppDomain==null)
            throw new NullPointerException("Share parameters not found in manifest ! com.phoenix.dynamic.domain should be declined !!");

        DynamicLink.SocialMetaTagParameters socialMetaTagParameters;
        DynamicLink.SocialMetaTagParameters.Builder socialMetaTagParametersBuilder = new DynamicLink.SocialMetaTagParameters.Builder();

        if (description!=null)
            socialMetaTagParametersBuilder.setDescription(description);
        if (title!=null)
            socialMetaTagParametersBuilder.setTitle(title);
        if (imageUri!=null)
            socialMetaTagParametersBuilder.setImageUrl(imageUri);
        socialMetaTagParameters = socialMetaTagParametersBuilder.build();

        DynamicLink.IosParameters iosParameters = null;
        DynamicLink.IosParameters.Builder iosParametersBuilder = null;
        if (mIosBundle!=null)
            iosParametersBuilder = new DynamicLink.IosParameters.Builder(mIosBundle);

        if (mAppStoreId != null && iosParametersBuilder !=null)
            iosParametersBuilder.setAppStoreId(mAppStoreId);

        if (iosParametersBuilder!=null)
            iosParameters = iosParametersBuilder.build();

        DynamicLink.GoogleAnalyticsParameters.Builder googleAnalyticsBuilder = new DynamicLink.GoogleAnalyticsParameters.Builder();
        if(campaign!=null)
            googleAnalyticsBuilder.setCampaign(campaign);
        if (medium!=null)
            googleAnalyticsBuilder.setMedium(medium);
        if (source!=null)
            googleAnalyticsBuilder.setSource(source);

        DynamicLink.AndroidParameters.Builder androidLinkBuilder;
        if (mAndroidPackage==null)
            androidLinkBuilder = new DynamicLink.AndroidParameters.Builder();
        else
            androidLinkBuilder = new DynamicLink.AndroidParameters.Builder(mAndroidPackage);

        if (mAndroidMinVersion!=null)
            androidLinkBuilder.setMinimumVersion(Integer.parseInt(mAndroidMinVersion));

        DynamicLink.Builder dynamicLinkBuilder=FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(url)
                .setDynamicLinkDomain(mAppDomain)
                .setAndroidParameters(androidLinkBuilder.build())
                .setGoogleAnalyticsParameters(googleAnalyticsBuilder.build())
                .setSocialMetaTagParameters(socialMetaTagParameters);

        if (iosParameters != null) {
            dynamicLinkBuilder.setIosParameters(iosParameters);
        }

        return dynamicLinkBuilder;
    }
}
