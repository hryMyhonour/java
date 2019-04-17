package aqs.demo;

import aqs.demo.lock.Lock;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;

@Slf4j
public class ConsumerProducer {
    Lock lock = new Lock();
    LinkedList<Integer> queue = new LinkedList<>();
    final int max = 10;
    Condition notFull = lock.newCondition();
    Condition notEmpty = lock.newCondition();

    void startConsumer() {
        new Thread(() -> {
            lock.lock();
            log.info("get lock");
            while (true) {
                if (queue.isEmpty()) {
                    try {
                        log.info("consumer notEmpty await");
                        notEmpty.await();
                        log.info("consumer notEmpty be signaled");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Integer good = queue.pollFirst();
                if (good != null) {
                    log.info("consumer get good");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    notFull.signal();
                }
            }
        }).start();
    }

    void startProducer() {
        new Thread(() -> {
            lock.lock();
            log.info("get lock");
            while (true) {
                if (queue.size() == max) {
                    try {
                        log.info("producer notFull await");
                        notFull.await();
                        log.info("producer notFull be signaled");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    queue.offer(1);
                    log.info("producer offer good");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    notEmpty.signal();
                }
            }
        }).start();
    }

    public static void main(String[] args) throws Exception {
        ConsumerProducer consumerProducer = new ConsumerProducer();
        consumerProducer.startConsumer();
        consumerProducer.startProducer();
        Thread.sleep(0);
    }

}
