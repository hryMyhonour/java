package threadpool.demo;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPool {
    private final int coreThreadSize;
    private final LinkedBlockingQueue<Runnable> waitingQueue;
    private final AtomicInteger coreSize = new AtomicInteger(0);

    public ThreadPool(int coreThreadSize, int maximumPoolSize) {
        this.coreThreadSize = coreThreadSize;
        waitingQueue = new LinkedBlockingQueue<>(maximumPoolSize);
    }

    public boolean execute(Runnable task) {
        for (; ; ) {
            int s = coreSize.get();
            if (s < coreThreadSize && coreSize.compareAndSet(s, s + 1)) {
                Worker w = new Worker(task);
                w.thread.start();
                return true;
            }
            return waitingQueue.offer(task);
        }
    }

    private Runnable takeTaskFromQueue() throws InterruptedException {
        return waitingQueue.take();
    }

    private final class Worker implements Runnable {
        final Thread thread;
        Runnable initTask;

        Worker(Runnable initTask) {
            this.initTask = initTask;
            thread = Executors.defaultThreadFactory().newThread(this);
        }

        @Override
        public void run() {
            Runnable task = this.initTask;
            try {
                while (task != null || (task = takeTaskFromQueue()) != null) {
                    try {
                        task.run();
                    } finally {
                        task = null;
                    }
                }
            } catch (Throwable e) {
                coreSize.decrementAndGet();
            }
        }
    }
}
