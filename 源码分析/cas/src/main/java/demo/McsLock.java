package demo;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 当一个线程需要资源并被阻塞时，会在其自身自旋检查资源可访问的状态。
 * 适合非一致存储访问系统（NUMA）
 */
public class McsLock implements Lock {

    private final static class Node {
        volatile boolean waiting;
        volatile Node next;
    }

    private AtomicReference<Node> tail = new AtomicReference<>(null);
    private ThreadLocal<Node> threadNode;

    public McsLock(){
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
        Node node = this.threadNode.get();
        Node pre = tail.getAndSet(node);
        if (pre != null){
            pre.next = node;
            node.waiting = true;
        }
        long duration = inTime ? unit.toNanos(time) : 0;
        while (node.waiting) {
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
        if (node.next == null){
            if (tail.compareAndSet(node, null)){
                return;
            }
            while (node.next == null){}
        }
        node.next.waiting = false;
        //至此，node节点已经不会再使用
        node.next = null;
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

}
