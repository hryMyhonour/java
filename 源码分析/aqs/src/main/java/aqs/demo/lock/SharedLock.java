package aqs.demo.lock;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;

public class SharedLock {

    static final class Mutex extends AbstractQueuedSynchronizer {
        final int max;

        Mutex(int max) {
            this.max = max;
        }

        @Override
        protected boolean tryAcquire(int arg) {
            return tryAcquireShared(1) >= 0;
        }

        @Override
        protected boolean tryRelease(int arg) {
            return tryReleaseShared(1);
        }

        @Override
        protected int tryAcquireShared(int arg) {
            int s = getState();
            if (max > s) {
                int n = s + arg;
                if(compareAndSetState(s, n)){
                    int remain = max - n;
                    System.out.println("tryAcquireShared, remain:" + remain);
                    return remain;
                }
            }
            return -1;
        }

        @Override
        protected boolean tryReleaseShared(int arg) {
            int s = getState();
            int n = s - arg;
            System.out.println("tryReleaseShared, remain:" + (this.max - n));
            return compareAndSetState(s, n);
        }

        @Override
        protected boolean isHeldExclusively() {
            return true;
        }
    }

    private final Mutex mutex;

    public SharedLock(int max) {
        this.mutex = new Mutex(max);
    }

    public void lock() {
        mutex.acquireShared(1);
    }

    public void unlock() {
        mutex.acquireShared(1);
    }
}
