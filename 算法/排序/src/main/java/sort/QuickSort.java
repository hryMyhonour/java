package sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QuickSort {
    public static <T> void sort(List<T> source, Comparator<T> comparator) {
        if (source == null || source.isEmpty()) {
            return;
        }
        s(source, comparator, 0, source.size() - 1);
    }

    private static <T> void s(List<T> source, Comparator<T> comparator, int start, int end) {
        int p = partition(source, comparator, start, end);
        if (p != start) {
            s(source, comparator, start, p - 1);
            s(source, comparator, p + 1, end);
        }
    }

    private static <T> int partition(List<T> source, Comparator<T> comparator, int start, int end) {
        T tag = source.get(start);
        while (start < end) {
            while (start < end && comparator.compare(source.get(end), tag) >= 0) {
                end--;
            }
            if (start < end) {
                Collections.swap(source, end, start);
            }
            while (start < end && comparator.compare(source.get(start), tag) <= 0) {
                start++;
            }
            if (start < end) {
                Collections.swap(source, start, end);
            }
        }
        return start;
    }
}
