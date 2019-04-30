package reference.demo;

import org.junit.Test;
import static org.junit.Assert.*;

public class SoftHashMapTest {

    @Test
    public void testMap(){
        SoftHashMap<Integer, Integer> softHashMap = new SoftHashMap<>(4);
        softHashMap.put(1,1);
        softHashMap.put(5,5);
        softHashMap.put(9,9);
        softHashMap.put(13,13);
        assertEquals(Integer.valueOf(1), softHashMap.get(1));
        assertEquals(Integer.valueOf(5), softHashMap.get(5));
        assertEquals(Integer.valueOf(9), softHashMap.get(9));
        assertEquals(Integer.valueOf(13), softHashMap.get(13));

    }
}
