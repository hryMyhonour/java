package aqs.demo;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class DemoTest {

    public static void main(String[] args) throws InterruptedException {
        HashMap<String, String> map = new HashMap<>();
        map.put("1","1");
        Iterator<String> i1 = map.values().iterator();
        Iterator<String> i2 = map.values().iterator();
        if (i1.hasNext())
        {
            i1.next();
            i1.remove();
        }
        System.out.println(i2.next());
    }
}
