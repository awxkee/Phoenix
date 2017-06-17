package com.github.dozzatq.phoenix.sort;

import android.support.annotation.NonNull;

import com.github.dozzatq.phoenix.tasks.Task;
import com.github.dozzatq.phoenix.tasks.TaskSource;
import com.github.dozzatq.phoenix.tasks.Tasks;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by dxfb on 17.06.2017.
 */

class QuicksortExecutor<T> {
    private T[] sortArray;
    private int length;
    private Executor executor;
    private Quicksort.Comparator<T> comparator;

    QuicksortExecutor(Executor executor, T[] sortArray, int length, Quicksort.Comparator<T> comparator) {
        if (executor == null)
            throw new NullPointerException("Executor must not be null!");
        if (sortArray == null)
            throw new NullPointerException("Sort array must not be null!");
        if (length < 0)
            throw new IllegalStateException("Length must be > 0!");

        this.sortArray = sortArray.clone();
        this.length = length;
        this.executor = executor;
        this.comparator = comparator;
        if (sortArray.length == 0) {
            return;
        }
        this.length = length;
    }

    T[] sortSync()
    {
        try {
            return taskSource.call();
        } catch (Exception e) {
            throw new RuntimeException("Crash with quicksort!");
        }
    }

    Task<T[]> sortAsync()
    {
        return Tasks.execute(executor, taskSource);
    }

    private TaskSource<T[]> taskSource = new TaskSource<T[]>() {
        @Override
        public T[] call() throws Exception {
            quicksort(0, length - 1, comparator);
            return sortArray;
        }
    };

    private void quicksort(int low, int high, @NonNull Quicksort.Comparator<T> comparator ) {
        int i = low, j = high;
        // Get the pivot element from the middle of the list
        T pivot = sortArray[low + (high-low)/2];

        // Divide into two lists
        while (i <= j) {
            // If the current value from the left list is smaller than the pivot
            // element then get the next element from the left list
            while (comparator.onCompare(sortArray[i], pivot) < 0) {
                i++;
            }
            // If the current value from the right list is larger than the pivot
            // element then get the next element from the right list
            while (comparator.onCompare(sortArray[i], pivot) > 0) {
                j--;
            }

            // If we have found a value in the left list which is larger than
            // the pivot element and if we have found a value in the right list
            // which is smaller than the pivot element then we exchange the
            // values.
            // As we are done we can increase i and j
            if (i <= j) {
                exchange(i, j);
                i++;
                j--;
            }
        }
        // Recursion
        if (low < j)
            quicksort(low, j, comparator);
        if (i < high)
            quicksort(i, high, comparator);
    }


    private void exchange(int i, int j) {
        T temp = sortArray[i];
        sortArray[i] = sortArray[j];
        sortArray[j] = temp;
    }
}
