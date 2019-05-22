package demo;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class CacheManagerTest {

    @Test
    public void testFIFO() {
        CacheManager cacheManager = new CacheManager(new FIFO(), 10);
        for (int i = 0; i < 20; i++) {
            cacheManager.add(new User(i, "User" + i));
        }
        assertNull(cacheManager.get(9));
        assertNotNull(cacheManager.get(10));
        assertEquals("User10", cacheManager.get(10).value());
    }

    @Test
    public void testLRU() {
        CacheManager cacheManager = new CacheManager(new LRU(), 10);
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < i - 1; j++) {
                cacheManager.get(j);
            }
            cacheManager.add(new User(i, "User" + i));
        }
        assertNull(cacheManager.get(9));
        assertNotNull(cacheManager.get(0));
        assertEquals("User0", cacheManager.get(0).value());
    }
}
