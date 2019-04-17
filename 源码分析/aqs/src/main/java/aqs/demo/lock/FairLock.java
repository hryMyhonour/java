package aqs.demo.lock;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class FairLock {

    private final Mutex mutex = new Mutex();

    static final class Mutex extends AbstractQueuedSynchronizer {
        @Override
        protected boolean tryAcquire(int arg) {
            if (hasQueuedPredecessors()){
                return false;
            }
            if(compareAndSetState(0, 1)){
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getExclusiveOwnerThread() != Thread.currentThread()){
                return false;
            }
            if (compareAndSetState(1, 0)){
                setExclusiveOwnerThread(null);
                return true;
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
