package com.github.dozzatq.phoenix;

import android.app.Application;

/**
 * Created by Rodion on 05.12.2016.
 */

public class PhoenixApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Phoenix.getInstance().setContext(getApplicationContext());
    }
}
