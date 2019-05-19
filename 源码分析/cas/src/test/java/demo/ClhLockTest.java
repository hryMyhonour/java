package demo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClhLockTest {

    public static void main(String[] args) throws Exception {
        ClhLock lock = new ClhLock();
        new Thread(() -> {
            lock.lock();
            log.info("{} get lock", Thread.currentThread().getId());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {

            } finally {
                log.info("{} will release lock", Thread.currentThread().getId());
                lock.unlock();
                log.info("{} released lock", Thread.currentThread().getId());
            }
        }).start();
        Thread.sleep(1000);
        lock.lock();
        log.info("{} get lock", Thread.currentThread().getId());
    }
}
