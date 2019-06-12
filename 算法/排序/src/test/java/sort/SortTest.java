package sort;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

public class SortTest {
    @Test
    public void testQuickSort() {
        Integer[] source = new Integer[]{120, -23, 560, 0, 324, 1, 1, 33};
        Integer[] copy = Arrays.copyOf(source, source.length);
        QuickSort.sort(source, Integer::compareTo);
        Arrays.sort(copy);
        assertArrayEquals(source, copy);
    }

    @Test
    public void testMergeSort() {
        Integer[] source = new Integer[]{120, -23, 560, 0, 324, 1, 1, 33};
        Integer[] copy = Arrays.copyOf(source, source.length);
        MergeSort.sort(source, Integer::compareTo);
        Arrays.sort(copy);
        assertArrayEquals(source, copy);
    }
}
