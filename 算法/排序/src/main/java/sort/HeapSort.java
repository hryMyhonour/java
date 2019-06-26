package sort;

import java.util.Comparator;

public class HeapSort {
    public static <T> void sort(T[] source, Comparator<T> comparator) {
        if (source == null || source.length == 0) {
            return;
        }
        int index = source.length - 1;
        for (int i = (index - 1) >> 1; i >= 0; i--) {
            maxHeapify(source, i, index, comparator);
        }
        //每次把最大值放到最后，然后再调正堆结构
        for (int i = index; i > 0; i--) {
            SortUtil.swap(source, 0, i);
            maxHeapify(source, 0, i - 1, comparator);
        }
    }


    //调整index处的顺序，使其符合大顶堆的规则
    private static <T> void maxHeapify(T[] source, int index, int lastIndex, Comparator<T> comparator) {
        int li = (index << 1) + 1;
        int ri = li + 1;
        if (li > lastIndex) {
            return;
        }
        int maxChildIndex = li;
        if (ri <= lastIndex && comparator.compare(source[li], source[ri]) < 0) {
            maxChildIndex = ri;
        }
        if (comparator.compare(source[index], source[maxChildIndex]) < 0) {
            SortUtil.swap(source, maxChildIndex, index);
            maxHeapify(source, maxChildIndex, lastIndex, comparator);
        }
    }
}
