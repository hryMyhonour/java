package aqs.demo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WaitNotifyInterruptDemo {
    private final Object monitor = new Object();
    private Thread t1 = new Thread(() -> {
        synchronized (monitor) {
            log.info("{} get monitor", Thread.currentThread().getId());
            try {
                log.info("{} wait", Thread.currentThread().getId());
                monitor.wait();
                log.info("{} wake up", Thread.currentThread().getId());
                log.info("{} interrupt signal is {}", Thread.currentThread().getId(), Thread.currentThread().isInterrupted());
            } catch (InterruptedException e) {
                log.error("{} interrupted", Thread.currentThread().getId(), e);
            }
            log.info("{} release monitor", Thread.currentThread().getId());
        }
    });

    /**
     * wait方法被唤醒后，需要再次获取对象锁
     *
     * @throws Exception
     */
    private void demo1() throws Exception {
        log.info("Demo1");
        t1.start();
        Thread.sleep(500);
        new Thread(() -> {
            synchronized (monitor) {
                log.info("{} get monitor", Thread.currentThread().getId());
                log.info("{} notify thread sync on monitor", Thread.currentThread().getId());
                monitor.notifyAll();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("{} interrupted", Thread.currentThread().getId(), e);
                }
                log.info("{} release monitor", Thread.currentThread().getId());
            }
        }).start();
    }

    /**
     * 线程被中断后，需要再次获取对象锁，才抛出终端异常
     *
     * @throws Exception
     */
    private void demo2() throws Exception {
        log.info("Demo2");
        t1.start();
        Thread.sleep(500);
        new Thread(() -> {
            synchronized (monitor) {
                log.info("{} get monitor", Thread.currentThread().getId());
                log.info("{} interrupt thread {}", Thread.currentThread().getId(), t1.getId());
                t1.interrupt();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("{} interrupted", Thread.currentThread().getId(), e);
                }
                log.info("{} release monitor", Thread.currentThread().getId());
            }
        }).start();
    }

    /**
     * 先中断了线程，再唤醒，此时肯定会被中断
     *
     * @throws Exception
     */
    private void demo3() throws Exception {
        log.info("Demo3");
        t1.start();
        Thread.sleep(500);
        new Thread(() -> {
            synchronized (monitor) {
                log.info("{} get monitor", Thread.currentThread().getId());
                log.info("{} interrupt thread {}", Thread.currentThread().getId(), t1.getId());
                t1.interrupt();
                log.info("{} notify thread {}", Thread.currentThread().getId(), t1.getId());
                monitor.notify();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("{} interrupted", Thread.currentThread().getId(), e);
                }
                log.info("{} release monitor", Thread.currentThread().getId());
            }
        }).start();
    }

    /**
     * 线程先被唤醒，唤醒时检查中断标志大概率尚未被设置为true，会继续运行
     *
     * @throws Exception
     */
    private void demo4() throws Exception {
        log.info("Demo4");
        t1.start();
        Thread.sleep(500);
        new Thread(() -> {
            synchronized (monitor) {
                log.info("{} get monitor", Thread.currentThread().getId());
                log.info("{} notify thread {}", Thread.currentThread().getId(), t1.getId());
                monitor.notify();
                log.info("{} interrupt thread {}", Thread.currentThread().getId(), t1.getId());
                t1.interrupt();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("{} interrupted", Thread.currentThread().getId(), e);
                }
                log.info("{} release monitor", Thread.currentThread().getId());
            }
        }).start();
    }

    public static void main(String[] args) throws Exception {
        WaitNotifyInterruptDemo demo = new WaitNotifyInterruptDemo();
        //demo.demo1();
        //demo.demo2();
        //demo.demo3();
        demo.demo4();
    }
}
