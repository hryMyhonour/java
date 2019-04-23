package threadpool.demo;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertTrue;

public class ThreadPoolTest {

    @Test
    public void testExecute() throws Exception {
        ThreadPool pool = new ThreadPool(1, 10);
        long start = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(1);
        pool.execute(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        });
        Thread.sleep(1000);
        pool.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        latch.await();
        long now = System.currentTimeMillis();
        assertTrue(now - start >= 6000);
    }
}
