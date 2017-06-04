package com.github.dozzatq.phoenix.Tasks;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by dxfb on 04.06.2017.
 */

public class DefaultExecutor implements Executor {

    private static volatile DefaultExecutor instance;

    public static DefaultExecutor getInstance() {
        DefaultExecutor localInstance = instance;
        if (localInstance == null) {
            synchronized (DefaultExecutor.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new DefaultExecutor();
                }
            }
        }
        return localInstance;
    }

    public static Executor CURRENT_THREAD_EXECUTOR = new Executor() {
        @Override
        public void execute(@NonNull Runnable command) {
            command.run();
        }
    };

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(@NonNull Runnable command) {
        handler.post(command);
    }

    public void executeDelayed(@NonNull Runnable command, int delay)
    {
        handler.postDelayed(command, delay);
    }
}
