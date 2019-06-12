package sort;

class SortUtil {
    static void swap(Object[] source, int i, int j) {
        Object o = source[i];
        source[i] = source[j];
        source[j] = o;
    }
}
