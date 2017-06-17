package com.github.dozzatq.phoenix.sort;

import android.support.annotation.NonNull;

import com.github.dozzatq.phoenix.tasks.MainThreadExecutor;
import com.github.dozzatq.phoenix.tasks.Task;
import com.github.dozzatq.phoenix.tasks.Tasks;

import java.util.List;
import java.util.concurrent.Executor;

public class Quicksort<T> {

    public List<T> sort(Executor executor, List<T> list, @NonNull Comparator<T> comparator) {
        QuicksortExecutorList<T> quicksortExecutor = new QuicksortExecutorList<T>(executor, list, list.size(), comparator);
        return quicksortExecutor.sortSync();
    }

    public List<T> sort(List<T> values, @NonNull Comparator<T> comparator) {
        QuicksortExecutorList<T> quicksortExecutor = new QuicksortExecutorList<T>(MainThreadExecutor.CURRENT_THREAD_EXECUTOR,
                values, values.size(), comparator);
        return quicksortExecutor.sortSync();
    }

    public Task<List<T>> sortAsync(List<T> values, @NonNull Comparator<T> comparator)
    {
        QuicksortExecutorList<T> quicksortExecutor = new QuicksortExecutorList<T>(Tasks.getDefaultExecutor(),
                values, values.size(), comparator);
        return quicksortExecutor.sortAsync();
    }

    public Task<List<T>> sortAsync(Executor executor, List<T> values, @NonNull Comparator<T> comparator)
    {
        QuicksortExecutorList<T> quicksortExecutor = new QuicksortExecutorList<T>(MainThreadExecutor.CURRENT_THREAD_EXECUTOR,
                values, values.size(), comparator);
        return quicksortExecutor.sortAsync();
    }

    public T[] sort(Executor executor, T[] values, @NonNull Comparator<T> comparator) {
        QuicksortExecutor<T> quicksortExecutor = new QuicksortExecutor<T>(executor, values, values.length, comparator);
        return quicksortExecutor.sortSync();
    }

    public T[] sort(T[] values, @NonNull Comparator<T> comparator) {
        QuicksortExecutor<T> quicksortExecutor = new QuicksortExecutor<T>(MainThreadExecutor.CURRENT_THREAD_EXECUTOR,
                values, values.length, comparator);
        return quicksortExecutor.sortSync();
    }

    public Task<T[]> sortAsync(T[] values, @NonNull Comparator<T> comparator)
    {
        QuicksortExecutor<T> quicksortExecutor = new QuicksortExecutor<T>(Tasks.getDefaultExecutor(),
                values, values.length, comparator);
        return quicksortExecutor.sortAsync();
    }



    public Task<T[]> sortAsync(Executor executor, T[] values, @NonNull Comparator<T> comparator)
    {
        QuicksortExecutor<T> quicksortExecutor = new QuicksortExecutor<T>(MainThreadExecutor.CURRENT_THREAD_EXECUTOR,
                values, values.length, comparator);
        return quicksortExecutor.sortAsync();
    }

    public interface Comparator<T>{
        public int onCompare(T t1, T t2);
    }

}