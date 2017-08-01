package com.github.dozzatq.phoenix.kernel;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Map;

/**
 * Created by Rodion Bartoshyk on 01.08.2017.
 */

public abstract class FCMExecutor {
    private Map<String, String> data;

    public FCMExecutor() {

    }

    public abstract void execute(Context context);

    public Map<String, String> getData()
    {
        return data;
    }

    void setData(@NonNull Map<String, String> data)
    {
        if (data==null)
            throw new NullPointerException("Map in push strategy must not be null !");
        this.data = data;
    }

    public abstract boolean isSuccessIndex();
}
