package com.github.dozzatq.phoenix.Fonts;

import android.graphics.Typeface;

import com.github.dozzatq.phoenix.Phoenix;

import java.util.Hashtable;

/**
 * Created by dxfb on 20.05.2017.
 */

public class PhoenixTypeface {
    private static final Hashtable<String, Typeface> typefaceCache = new Hashtable<>();

    public static Typeface getTypeface(String assetPath) {
        synchronized (typefaceCache) {
            if (!typefaceCache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(Phoenix.getInstance().getAssets(), assetPath);
                    typefaceCache.put(assetPath, t);
                } catch (Exception e) {

                    return null;
                }
            }
            return typefaceCache.get(assetPath);
        }
    }

}
