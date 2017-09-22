package com.github.dozzatq.phoenix.tasks;

/**
 * Created by Rodion Bartoshik on 10.06.2017.
 */

public abstract class CancellableSource<PState> extends TaskCompletionSource<PState> {
    private CancellableTask<PState> task = new CancellableTask<PState>() {
        @Override
        public boolean cancel() {
            synchronized (mLock) {
                boolean result = CancellableSource.this.cancel();
                task.notifyListeners();
                return result;
            }
        }

        @Override
        public boolean isCanceled() {
            synchronized (mLock) {
                return CancellableSource.this.isCanceled();
            }
        }

        @Override
        public boolean isInProgress() {
            synchronized (mLock) {
                return CancellableSource.this.isInProgress();
            }
        }

        @Override
        public PState getProgress() {
            synchronized (mLock) {
                return CancellableSource.this.getProgress();
            }
        }
    };

    @Override
    public final String getTag()
    {
        return "CancellableSource";
    }

    @Override
    public String toString()
    {
        if (isInProgress())
            return new StringBuilder(getTag()).append(" state task in progress").toString();
        else if (isCanceled())
            return new StringBuilder(getTag()).append(" state task canceled").toString();
        else return super.toString();
    }

    @Override
    public final CancellableTask<PState> getTask()
    {
        return task;
    }

    public final void refreshListenersProgress()
    {
        task.notifyListeners();
    }

    public abstract boolean cancel();

    public abstract boolean isCanceled();

    public abstract boolean isInProgress();

    public abstract PState getProgress();
}
