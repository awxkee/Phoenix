package com.github.dozzatq.phoenix.Tasks;

/**
 * Created by dxfb on 10.06.2017.
 */

public abstract class CancellableSource<PState> extends TaskCompletionSource<PState> {
    private CancellableTask<PState> task = new CancellableTask<PState>() {
        @Override
        public boolean cancel() {
            synchronized (waitObject) {
                boolean result = CancellableSource.this.cancel();
                notifyChangedState();
                return result;
            }
        }

        @Override
        public boolean isCanceled() {
            synchronized (waitObject) {
                return CancellableSource.this.isCanceled();
            }
        }

        @Override
        public boolean isInProgress() {
            synchronized (waitObject) {
                return CancellableSource.this.isInProgress();
            }
        }

        @Override
        public PState getProgress() {
            synchronized (waitObject) {
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
        task.notifyChangedState();
    }

    public abstract boolean cancel();

    public abstract boolean isCanceled();

    public abstract boolean isInProgress();

    public abstract PState getProgress();
}
