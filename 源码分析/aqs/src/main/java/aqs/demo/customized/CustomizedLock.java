package aqs.demo.customized;

public class CustomizedLock {
    private final static class Mutex extends CustomizedAqs {
        @Override
        boolean tryAcquire(int state) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        boolean tryRelease(int state) {
            if (getExclusiveOwnerThread() != Thread.currentThread()) {
                return false;
            }
            if (compareAndSetState(1, 0)) {
                setExclusiveOwnerThread(null);
                return true;
            }
            return false;
        }
    }

    private final Mutex mutex = new Mutex();

    public void lock() {
        mutex.acquire(1);
    }

    public void unlock() {
        mutex.release(0);
    }
}
