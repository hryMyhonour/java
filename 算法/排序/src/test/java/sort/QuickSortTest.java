package sort;

import com.google.common.collect.Lists;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuickSortTest {
    @Test
    public void test(){
        List<Integer> source = Lists.newArrayList(120,-23,560,0,324,1,1,33);
        List<Integer> copy = Lists.newArrayList(source);
        QuickSort.sort(source, Integer::compareTo);
        Collections.sort(copy);
        System.out.println(source);
        assertEquals(source.hashCode(), copy.hashCode());

        QuickSort.sort(new ArrayList<>(), Integer::compareTo);
        QuickSort.sort(null, Integer::compareTo);
    }
}
