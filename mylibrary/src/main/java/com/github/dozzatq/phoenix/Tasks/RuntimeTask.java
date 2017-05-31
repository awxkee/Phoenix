package com.github.dozzatq.phoenix.Tasks;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by rodeon on 5/26/17.
 */

public class RuntimeTask<PResult, ZResult> extends Task<PResult> {

    private ArrayDeque<OnPublishListener<ZResult>> onPublishListener;
    private ZResult zResult;
    private ThreadPoolExecutor executor;
    private boolean isMayPublish;

    public RuntimeTask()
    {
        onPublishListener = new ArrayDeque<>();
    }

    public void setPublish(ZResult result)
    {
        synchronized (synchronizedObject) {
            zResult = result;
            isMayPublish = true;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyPublishListener();;
                }
            });
        }
    }

    @NonNull
    @Override
    public RuntimeTask<PResult, ZResult> addOnSuccessListener(@NonNull OnSuccessListener<PResult> listener) {
        return (RuntimeTask<PResult, ZResult>) super.addOnSuccessListener(listener);
    }

    @NonNull
    @Override
    public RuntimeTask<PResult, ZResult> addOnCompleteListener(@NonNull OnCompleteListener<PResult> listener) {
        return (RuntimeTask<PResult, ZResult>) super.addOnCompleteListener(listener);
    }

    @NonNull
    @Override
    public RuntimeTask<PResult, ZResult> addOnFailureListener(@NonNull OnFailureListener listener) {
        return (RuntimeTask<PResult, ZResult>) super.addOnFailureListener(listener);
    }

    public ZResult getPublish()
    {
        synchronized (synchronizedObject) {
            if (isMayPublish()) {
                return zResult;
            } else return null;
        }
    }

    private void notifyPublishListener()
    {
        if (isMayPublish()) {
            for (final OnPublishListener<ZResult> listener : onPublishListener) {
                try {
                     listener.OnPublish(getPublish());
                } catch (Exception e1) {
                    Log.d("Task<PResult>", "Bad Publish Listener");
                }
            }
        }
    }

    @NonNull
    public RuntimeTask<PResult, ZResult> addOnPublishListener(@NonNull final OnPublishListener<ZResult> listener)
    {
        synchronized (synchronizedObject) {
            onPublishListener.add(listener);
            if (isMayPublish()) {
                try {
                    Runnable publishRunnable = new Runnable() {
                        @Override
                        public void run() {
                            listener.OnPublish(getPublish());
                        }
                    };
                    handler.post(publishRunnable);
                } catch (Exception e) {
                    Log.d("Task<PResult>", "Bad Publish Listener");
                }
            }
            return this;
        }
    }

    public boolean isMayPublish() {
        synchronized (synchronizedObject) {
            return isMayPublish && !isExcepted();
        }
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }


}
