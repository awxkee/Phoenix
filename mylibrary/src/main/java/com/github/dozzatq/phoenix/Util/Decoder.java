package com.github.dozzatq.phoenix.Util;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.nio.charset.StandardCharsets;

/**
 * Created by rodion on 17.11.16.
 */

public class Decoder {
    public static String decode(@Nullable String horoscope)
    {
        if (horoscope==null)
            return "";
        if (horoscope.equals("") || horoscope.length()==0)
            return "";
        return nativeDecodeKitKat(horoscope);
    }

    private static String nativeDecodeKitKat(@NonNull String horoscope)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                return new String(Base64.decode(horoscope, Base64.DEFAULT), StandardCharsets.UTF_8);
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                return nativeDecodePreKitKat(horoscope);
            }
        }
        else return nativeDecodePreKitKat(horoscope);
    }

    private static String nativeDecodePreKitKat(@NonNull String horoscope)
    {
        try {
            return new String(Base64.decode(horoscope, Base64.DEFAULT), "UTF-8");
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }
}
