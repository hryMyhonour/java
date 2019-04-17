package aqs.demo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * 由于CopyOnWriteArrayList的特性，可能会发生一些并发修改的问题
 */
public class CopyOnWriteArrayListErrorDemo {
    private static final List<Integer> list = new CopyOnWriteArrayList<>();
    private static final CountDownLatch latch1 = new CountDownLatch(1);
    private static final CountDownLatch latch2 = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {
        list.add(1);
        list.add(3);
        new Thread(() -> {
            try {
                latch1.await();
                list.add(1, 2);
                latch2.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        for (int i = 0; i < list.size(); i++) {
            if (i == 1) {
                latch1.countDown();
            }
            System.out.println(list.get(i));
            if (i == 1) {
                latch2.await();
            }
        }
    }
}
