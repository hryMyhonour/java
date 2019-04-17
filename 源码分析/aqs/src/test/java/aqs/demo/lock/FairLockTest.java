package aqs.demo.lock;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class FairLockTest {

    @Test
    public void testLock() throws Exception{
        CountDownLatch latch = new CountDownLatch(3);
        Lock lock = new Lock();
        new Thread(()->{
            lock.lock();
            log.info("delay 0 second, sub thread get lock");
            try {
                int i = 0;
                while (i++<3){
                    log.info("sub thread sleep {} second", i);
                    Thread.sleep(1000);
                }
            }catch (InterruptedException e){
                log.error(e.getMessage());
            }finally {
                lock.unlock();
            }
            log.info("sub thread unlock");
            latch.countDown();
        }).start();
        new Thread(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
            lock.lock();
            log.info("delay 1 second, sub thread get lock");
            try {
                int i = 0;
                while (i++<3){
                    log.info("sub thread sleep {} second", i);
                    Thread.sleep(1000);
                }
            }catch (InterruptedException e){
                log.error(e.getMessage());
            }finally {
                lock.unlock();
            }
            log.info("sub thread unlock");
            latch.countDown();
        }).start();
        new Thread(()->{
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
            lock.lock();
            log.info("delay 2 second, sub thread get lock");
            try {
                int i = 0;
                while (i++<3){
                    log.info("sub thread sleep {} second", i);
                    Thread.sleep(1000);
                }
            }catch (InterruptedException e){
                log.error(e.getMessage());
            }finally {
                lock.unlock();
            }
            log.info("sub thread unlock");
            latch.countDown();
        }).start();
        latch.await();
    }
}
