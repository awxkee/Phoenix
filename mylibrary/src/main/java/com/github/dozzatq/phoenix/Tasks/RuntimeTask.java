package com.github.dozzatq.phoenix.Tasks;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by rodeon on 5/26/17.
 */

public class RuntimeTask<PResult, ZResult> extends Task<PResult> {

    private List<OnPublishListener<ZResult>> onPublishListener;
    private Handler obtainHandler;
    private ZResult zResult;
    private ThreadPoolExecutor executor;
    private boolean isMayPublish;

    public RuntimeTask()
    {
        obtainHandler = new Handler(Looper.getMainLooper());
        onPublishListener = Collections.synchronizedList(new ArrayList<OnPublishListener<ZResult>>());
    }

    public void setPublish(ZResult result)
    {
        zResult = result;
        isMayPublish = true;
        notifyPublishListener();
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
        if (isMayPublish())
        {
            return zResult;
        }
        else return null;
    }

    private void notifyPublishListener()
    {
        if (isMayPublish()) {
            for (final OnPublishListener<ZResult> listener : onPublishListener) {
                try {
                    Runnable publishRunnable = new Runnable() {
                        @Override
                        public void run() {
                            listener.OnPublish(getPublish());
                        }
                    };
                    if (getExecutor()!=null) {
                        getExecutor().submit(publishRunnable);
                    }else {
                        obtainHandler.post(publishRunnable);
                    }
                } catch (Exception e1) {
                    Log.d("Task<PResult>", "Bad Publish Listener");
                }
            }
        }
    }

    @NonNull
    public RuntimeTask<PResult, ZResult> addOnPublishListener(@NonNull final OnPublishListener<ZResult> listener)
    {
        onPublishListener.add(listener);
        if (isMayPublish())
        {
            try{
                Runnable publishRunnable = new Runnable() {
                    @Override
                    public void run() {
                        listener.OnPublish(getPublish());
                    }
                };
                if (getExecutor()!=null) {
                    getExecutor().submit(publishRunnable);
                }else {
                    obtainHandler.post(publishRunnable);
                }
                }
            catch (Exception e){
                Log.d("Task<PResult>", "Bad Complete Listener");
            }
        }
        return this;
    }

    public boolean isMayPublish() {
        return isMayPublish && !isExcepted();
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    public interface OnPublishListener<ZResult> {
        void OnPublish(ZResult pResult);
    }

}
