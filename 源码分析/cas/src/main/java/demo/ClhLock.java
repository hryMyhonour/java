package demo;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 当一个线程需要资源并被阻塞时，会检查父节点的资源可访问的状态。
 */
public class ClhLock implements Lock {

    private final static class Node {
        volatile boolean locked;
    }

    private AtomicReference<Node> tail = new AtomicReference<>(new Node());
    private ThreadLocal<Node> threadNode;

    public ClhLock() {
        threadNode = ThreadLocal.withInitial(Node::new);
    }

    public void lock() {
        try {
            this.tryLock(false, false, 0, null);
        } catch (InterruptedException e) {
            // will not throw
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        this.tryLock(false, true, 0, null);
    }

    @Override
    public boolean tryLock() {
        try {
            return this.tryLock(true, false, 0, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            // will not throw
            return false;
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return this.tryLock(true, true, time, unit);
    }

    private boolean tryLock(boolean inTime, boolean interruptable, long time, TimeUnit unit) throws InterruptedException {
        long cur = System.nanoTime();
        Node node = threadNode.get();
        node.locked = true;
        Node pre = tail.getAndSet(node);
        long duration = inTime ? unit.toNanos(time) : 0;
        while (pre.locked) {
            if (inTime) {
                long now = System.currentTimeMillis();
                if (now - cur > duration) {
                    return false;
                }
            }
            if (interruptable && Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
        return true;
    }

    @Override
    public void unlock() {
        Node node = threadNode.get();
        node.locked = false;
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

}
