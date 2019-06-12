package sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QuickSort {
    public static <T> void sort(T[] source, Comparator<T> comparator) {
        if (source == null || source.length == 0) {
            return;
        }
        s(source, comparator, 0, source.length - 1);
    }

    private static <T> void s(T[]  source, Comparator<T> comparator, int start, int end) {
        int p = partition(source, comparator, start, end);
        if (p != start) {
            s(source, comparator, start, p - 1);
            s(source, comparator, p + 1, end);
        }
    }

    private static <T> int partition(T[]  source, Comparator<T> comparator, int start, int end) {
        T tag = source[start];
        while (start < end) {
            while (start < end && comparator.compare(source[end], tag) >= 0) {
                end--;
            }
            if (start < end) {

                SortUtil.swap(source, end, start);
            }
            while (start < end && comparator.compare(source[start], tag) <= 0) {
                start++;
            }
            if (start < end) {
                SortUtil.swap(source, start, end);
            }
        }
        return start;
    }
}
