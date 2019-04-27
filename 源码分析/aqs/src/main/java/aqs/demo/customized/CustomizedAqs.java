package aqs.demo.customized;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.locks.AbstractOwnableSynchronizer;
import java.util.concurrent.locks.LockSupport;

public abstract class CustomizedAqs extends AbstractOwnableSynchronizer {

    private static final class Node {
        /**
         * waitStatus value to indicate thread has cancelled
         */
        static final int CANCELLED = 1;
        /**
         * waitStatus value to indicate successor's thread needs unparking
         */
        static final int SIGNAL = -1;
        /**
         * waitStatus value to indicate thread is waiting on condition
         */
        static final int CONDITION = -2;
        /**
         * waitStatus value to indicate the next acquireShared should
         * unconditionally propagate
         */
        static final int PROPAGATE = -3;

        volatile Thread thread;
        volatile Node predecessor;
        volatile Node successor;
        volatile int waitStatus;

        Node(Thread thread) {
            this.thread = thread;
        }

        Node() {
        }
    }

    private static final Unsafe unsafe;
    private static final long stateOffset;
    private static final long headOffset;
    private static final long tailOffset;
    private static final long waitStatusOffset;
    private static final long nextOffset;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            stateOffset = unsafe.objectFieldOffset(CustomizedAqs.class.getDeclaredField("state"));
            headOffset = unsafe.objectFieldOffset(CustomizedAqs.class.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset(CustomizedAqs.class.getDeclaredField("tail"));
            waitStatusOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("waitStatus"));
            nextOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("successor"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    /**
     * 等待队列的头节点，这里要注意头节点是一个标记节点，是等待队列的一个数据结构，
     * 并不意味着当前资源被占用中，如果一个节点的前驱是头节点，那么说明该节点有资格去获取资源。
     */
    private volatile Node head;
    private volatile Node tail;
    private volatile int state;


    abstract boolean tryAcquire(int state);

    abstract boolean tryRelease(int state);

    /**
     * 阻塞式获取资源
     *
     * @param state 资源信号
     */
    public final void acquire(int state) {
        if (!tryAcquire(state)
                && acquireQueue(addNode(state), state)) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 释放资源，唤醒后继node
     *
     * @param state 资源信号
     */
    public final boolean release(int state) {
        if (tryRelease(state)) {
            Node head = this.head;
            if (head != null && head.waitStatus != 0) {
                unparkSuccessor(head);
            }
            return true;
        }
        return false;
    }

    /**
     * 进入等待队列尝试获取资源，该方法会挂起线程，直到获取到资源或者等待被中断。
     * 中断的返回，需要等待线程被唤醒并获取到资源
     *
     * @param node 需要新加入的node
     * @return true 线程已经被中断，false 线程未被中断
     */
    private boolean acquireQueue(Node node, int state) {
        boolean failed = true;//等待资源失败标记
        try {
            boolean interrupted = false;//中断标记
            // CAS检查是否可获得资源，如果不可获取，则阻塞线程
            while (true) {
                Node pre = node.predecessor;
                //如果节点的前驱是头节点，则检查是否可获取资源
                if (pre == head && tryAcquire(state)) {
                    //当前节点获得了资源，升级为头节点
                    head = node;
                    node.predecessor = null;
                    node.thread = null;
                    failed = false;
                    return interrupted;
                }
                if (readyParkAfterAcquireFailed(pre, node) && parkAndCheckInterrupted()) {
                    interrupted = true;
                }
            }
        } finally {
            //说明该线程已经退出了等待，无论是获取到资源，还是发生异常或者被中断
            if (failed) {
                //如果是异常或者中断退出，则将该节点从队列中删除
                cancelNode(node);
            }
        }
    }

    /**
     * 检查、更新节点前驱的状态
     *
     * @param predecessor 节点的前驱
     * @param node        节点
     * @return true 节点已经准备好进入阻塞状态
     */
    private boolean readyParkAfterAcquireFailed(Node predecessor, Node node) {
        int waiteState = predecessor.waitStatus;
        if (waiteState == Node.SIGNAL) {
            return true;
        }
        if (waiteState > 0) {
            do {
                node.predecessor = predecessor = predecessor.predecessor;
            } while (predecessor.waitStatus > 0);
            predecessor.successor = node;
        } else {
            compareAndSetNodeWaitState(predecessor, waiteState, Node.SIGNAL);
        }
        return false;
    }

    /**
     * 挂起线程
     *
     * @return 线程是否被中断
     */
    private boolean parkAndCheckInterrupted() {
        LockSupport.park(this);
        return Thread.interrupted();
    }

    /**
     * 将节点从等待队列中移除
     *
     * @param node 需要移除的节点
     */
    private void cancelNode(Node node) {
        if (node == null)
            return;
        node.thread = null;
        // 找到该节点有效的前驱节点
        Node pred = node.predecessor;
        while (pred.waitStatus > 0)
            node.predecessor = pred = pred.predecessor;
        // 有效前驱节点的后继，用于稍后的CAS更新其后记
        Node next = pred.successor;
        // 标记节点的状态，这样在从处理等待队列的时候，会跳过该节点
        node.waitStatus = Node.CANCELLED;

        //如果该节点是尾节点，则将前驱设置为等待队列的尾节点
        if (node == tail && compareAndSetTail(node, pred)) {
            compareAndSetNodeSuccessor(pred, next, null);
        } else {
            // 将该节点的后继连接到有效前驱
            int ws;
            if (pred != head &&
                    ((ws = pred.waitStatus) == Node.SIGNAL ||
                            (ws <= 0 && compareAndSetNodeWaitState(pred, ws, Node.SIGNAL))) &&
                    pred.thread != null) {
                Node successor = node.successor;
                if (successor != null && successor.waitStatus <= 0)
                    compareAndSetNodeSuccessor(pred, next, successor);
            } else {
                // 如果前驱是头节点或者前驱无效，则唤醒后继
                unparkSuccessor(node);
            }
        }
    }

    /**
     * 唤醒一个节点的后继节点线程
     *
     * @param node 父节点
     */
    private void unparkSuccessor(Node node) {
        Node s = node.successor;
        //如果后继节点无效，则从队尾到node之间查找最靠近node的一个有效节点
        if (s == null || s.waitStatus > 0) {
            s = null;
            for (Node t = tail; t != null && t != node; t = t.predecessor)
                if (t.waitStatus <= 0)
                    s = t;
        }
        if (s != null)
            LockSupport.unpark(s.thread);
    }

    /**
     * 将当前线程加入到等待队列
     *
     * @param state 资源信号
     * @return 当前线程的node
     */
    private Node addNode(int state) {
        Node node = new Node(Thread.currentThread());
        //尝试快速入队，直接将节点加入到队尾
        Node predecessor = tail;
        if (predecessor != null && compareAndSetTail(predecessor, node)) {
            predecessor.successor = node;
            node.predecessor = predecessor;
            return node;
        }
        //如果快速入队失败，则CAS入队
        while (true) {
            Node pre = tail;
            if (pre == null) {
                if (compareAndSetHead(null, new Node())) {
                    tail = head;
                }
            } else {
                if (compareAndSetTail(pre, node)) {
                    pre.successor = node;
                    node.predecessor = pre;
                    break;
                }
            }
        }
        return node;
    }

    private boolean compareAndSetHead(Node expect, Node newHead) {
        return unsafe.compareAndSwapObject(this, headOffset, expect, newHead);
    }

    private boolean compareAndSetTail(Node expect, Node newTail) {
        return unsafe.compareAndSwapObject(this, tailOffset, expect, newTail);
    }

    boolean compareAndSetState(int expect, int newState) {
        return unsafe.compareAndSwapInt(this, stateOffset, expect, newState);
    }

    private boolean compareAndSetNodeWaitState(Node node, int expect, int newState) {
        return unsafe.compareAndSwapInt(node, waitStatusOffset, expect, newState);
    }

    private boolean compareAndSetNodeSuccessor(Node node, Node expect, Node newSuccessor) {
        return unsafe.compareAndSwapObject(node, nextOffset, expect, newSuccessor);
    }

}
