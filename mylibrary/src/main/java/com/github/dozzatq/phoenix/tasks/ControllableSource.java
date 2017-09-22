package com.github.dozzatq.phoenix.tasks;

/**
 * Created by Rodion Bartoshik on 10.06.2017.
 */

public abstract class ControllableSource<PState> extends TaskCompletionSource<PState> {
    private ControllableTask<PState> task = new ControllableTask<PState>() {
        @Override
        public boolean pause() {
            synchronized (mLock) {
                boolean result = ControllableSource.this.pause();
                notifyListeners();
                return result;
            }
        }

        @Override
        public boolean resume() {
            synchronized (mLock) {
                return ControllableSource.this.resume();
            }
        }

        @Override
        public boolean isPaused() {
            synchronized (mLock) {
                return ControllableSource.this.isPaused();
            }
        }

        @Override
        public boolean cancel() {
            synchronized (mLock) {
                boolean result = ControllableSource.this.cancel();
                task.notifyListeners();
                return result;
            }
        }

        @Override
        public boolean isCanceled() {
            synchronized (mLock) {
                return ControllableSource.this.isCanceled();
            }
        }

        @Override
        public boolean isInProgress() {
            synchronized (mLock) {
                return ControllableSource.this.isInProgress();
            }
        }

        @Override
        public PState getProgress() {
            synchronized (mLock) {
                return ControllableSource.this.getProgress();
            }
        }
    };

    @Override
    public final String getTag()
    {
        return "ControllableSource";
    }

    @Override
    public String toString()
    {
        if (isPaused())
            return new StringBuilder(getTag()).append(" state task in pause, waiting for resume()").toString();
        if (isInProgress())
            return new StringBuilder(getTag()).append(" state task in progress").toString();
        else if (isCanceled())
            return new StringBuilder(getTag()).append(" state task canceled").toString();
        else
            return super.toString();
    }

    @Override
    public final ControllableTask<PState> getTask(){
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

    public abstract boolean pause();

    public abstract boolean resume();

    public abstract boolean isPaused();
}
