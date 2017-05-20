package com.github.dozzatq.phoenix.share;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by RondailP on 04.10.2016.
 */
public class PhoenixShare {
    private Intent shareIntent;
    private AppCompatActivity appContext;
    private HashMap<String, CustomShareCallback> callbackHashMap = new HashMap<>();
    private BroadcastReceiver broadcastReceiver;
    private final static String SEND_PARAMS_FORMAT = ".PhoenixShare:SEND_ID:%d";
    private String calledReceiver;
    public final static String SEND_SHARE_PARAM_RECEIVER = ".PhoenixShare:Receiver";
    public final static String SEND_SHARE_TYPE = ".PhoenixShare:Type";
    public final static String SEND_SHARE_ACTION = ".PhoenixShare:Action";
    public final static String SEND_SELECTED_ACTION = ".PhoenixShare:SelectedAction";
    public final static String SEND_SHARE_TITLE = ".PhoenixShare:Title";

    public PhoenixShare(@NonNull AppCompatActivity appContext, @NonNull Intent shareIntent)
    {
        this.shareIntent = shareIntent;
        try {
            this.appContext = appContext;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException("Share activity must extends AppCompatActivity");
        }
    }

    public void addCallback(String packageS, CustomShareCallback customShareCallback)
    {
        callbackHashMap.put(packageS, customShareCallback);
    }

    public String getVkPackageName()
    {
        return "com.vkontakte.android";
    }

    public String getFacebookName () { return  "com.facebook.katana"; }

    public void show()
    {
        show(null);
    }

    public void show(@Nullable String title)
    {
        Intent intent = new Intent(appContext, PhoenixShareActivity.class);
        intent.putExtras(shareIntent.getExtras());
        calledReceiver = String.format(Locale.US, SEND_PARAMS_FORMAT, System.currentTimeMillis());
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String selectedPackage = intent.getStringExtra(SEND_SELECTED_ACTION);
                if (!callbackHashMap.containsKey(selectedPackage))
                {
                    shareIntent.setPackage(selectedPackage);
                    appContext.startActivity(shareIntent);
                }
                else {
                    if (!callbackHashMap.get(selectedPackage).shareSelected(selectedPackage, shareIntent))
                    {
                        shareIntent.setPackage(selectedPackage);
                        appContext.startActivity(shareIntent);
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(appContext).registerReceiver(broadcastReceiver,
                new IntentFilter(calledReceiver));
        intent.putExtra(SEND_SHARE_PARAM_RECEIVER, calledReceiver);
        intent.putExtra(SEND_SHARE_TYPE, shareIntent.getType());
        intent.putExtra(SEND_SHARE_ACTION, shareIntent.getAction());
        intent.putExtra(SEND_SHARE_TITLE, title);
        startCompatActivity(appContext, intent);
    }

    public Intent getShareIntent() {
        return shareIntent;
    }

    public void setShareIntent(Intent shareIntent) {
        this.shareIntent = shareIntent;
    }

    public interface CustomShareCallback{
        public boolean shareSelected(String selectedPackage, Intent sharedIntent);
    }

    public static void startCompatActivity(AppCompatActivity activity , Intent intent)
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(activity);
            try {
                activity.startActivity(intent, activityOptions.toBundle());
            }
            catch (IllegalArgumentException e)
            {
                activity.startActivity(intent);
            }
        }else {
            activity.startActivity(intent);
        }
    }
}
