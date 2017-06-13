package com.github.dozzatq.phoenix.util;

import android.support.annotation.NonNull;
import android.util.Base64;

/**
 * Created by rodion on 20.11.16.
 */

public class Encoder {
    public static String encode(@NonNull String potential)
    {
        try {
            return new String(Base64.encode(potential.getBytes(), Base64.DEFAULT));
        }
        catch (Exception e)
        {
            return "";
        }
    }
}
