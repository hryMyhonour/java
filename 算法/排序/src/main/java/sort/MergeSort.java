package sort;

import java.util.Comparator;

public class MergeSort {
    public static <T> void sort(T[] source, Comparator<T> comparator) {
        if (source == null || source.length == 0) {
            return;
        }
        Object[] cache = new Object[source.length];
        divide(source, 0, source.length - 1, cache, comparator);
    }

    private static <T> void divide(T[] source, int s, int e, Object[] cache, Comparator<T> comparator) {
        if (s < e) {
            int m = (s + e) / 2;
            divide(source, s, m, cache, comparator);
            divide(source, m + 1, e, cache, comparator);
            merge(source, s, e, cache, comparator);
        }
    }

    private static <T> void merge(T[] source, int s, int e, Object[] cache, Comparator<T> comparator) {
        if (s == e) {
            cache[s] = source[s];
            return;
        }
        int m = (s + e) / 2;
        int si = s;
        int ei = m + 1;
        int i = s;
        while (si <= m && ei <= e) {
            if (comparator.compare(source[si], source[ei]) > 0) {
                cache[i++] = source[ei++];
            } else {
                cache[i++] = source[si++];
            }
        }
        while (si <= m) {
            cache[i++] = source[si++];
        }
        while (ei <= e) {
            cache[i++] = source[ei++];
        }
        while (s <= e) {
            source[s] = (T) cache[s];
            s++;
        }
    }
}
