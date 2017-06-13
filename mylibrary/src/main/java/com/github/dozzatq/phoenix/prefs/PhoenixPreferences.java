package com.github.dozzatq.phoenix.prefs;

/**
 * Created by RondailP on 09.09.2016.
 */
public class PhoenixPreferences extends PhoenixPrefsScheme {
    private static PhoenixPreferences ourInstance = null;
    public static PhoenixPreferences getInstance() {
        PhoenixPreferences localInstance = ourInstance;
        if (localInstance == null) {
            synchronized (PhoenixPreferences.class) {
                localInstance = ourInstance;
                if (localInstance == null) {
                    ourInstance = localInstance = new PhoenixPreferences();
                }
            }
        }
        return localInstance;
    }

    private PhoenixPreferences(){
        super(null, -1);
    }

    public PhoenixPrefsScheme getScheme(String prefsName, int prefsMode)
    {
        return new PhoenixPrefsScheme(prefsName, prefsMode);
    }

    public PhoenixPrefsScheme getDefaultScheme()
    {
        return this;
    }

}
