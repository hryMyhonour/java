package aqs.demo.lock;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class ReentrantLock {
    private Mutex mutex = new Mutex();

    private static final class Mutex extends AbstractQueuedSynchronizer {
        @Override
        protected boolean tryAcquire(int arg) {
            if (Thread.currentThread().equals(getExclusiveOwnerThread())){
                setState(getState()+1);
                return true;
            }
            if (compareAndSetState(0, 1)){
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (Thread.currentThread().equals(getExclusiveOwnerThread())){
                int state = getState();
                if (compareAndSetState(state, state - 1)){
                    if (state == 1) {
                        setExclusiveOwnerThread(null);
                    }
                    return true;
                }
            }
            return false;
        }
    }

    public void lock(){
        mutex.acquire(1);
    }

    public void unlock(){
        mutex.release(1);
    }
}
