package zookeeper;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@Slf4j
public class DistributedLock implements Lock {

    private final CuratorFramework client;
    private final String basePath;
    private String selfPath;
    private static final String LOCK_NAME = "LOCK";
    private final ThreadLocal<ThreadContext> threadLocal = new ThreadLocal<>();

    private static class ThreadContext {
        private int lockCount;

        public ThreadContext(int lockCount) {
            this.lockCount = lockCount;
        }
    }

    private final Watcher watcher = event -> {
        log.info("watch event: {}", event.getPath());
        synchronized (DistributedLock.this) {
            DistributedLock.this.notifyAll();
        }
    };

    public DistributedLock(CuratorFramework client, String basePath) {
        this.client = client;
        this.basePath = basePath;
    }

    @Override
    public void lock() {
        this.tryLock(-1, TimeUnit.MILLISECONDS);
    }

    @Override
    public void lockInterruptibly() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock() {
        return this.tryLock(0, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        //当前线程正在持有锁
        ThreadContext threadContext = threadLocal.get();
        if (threadContext != null) {
            threadContext.lockCount++;
            return true;
        }
        //等待时间的处理
        long startTime = System.currentTimeMillis();
        long waitTime = unit.toMillis(time);
        boolean getLock = false;
        //生成候选人结点
        try {
            String path = ZKPaths.makePath(basePath, LOCK_NAME);
            selfPath = client
                    .create()
                    .creatingParentsIfNeeded()
                    .withProtection()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(path);
            log.info("lock self path {}", selfPath);
        } catch (Exception exception) {
            deleteSelf();
            throw new RuntimeException(exception);
        }
        while (true) {
            try {
                //获取所有的候选人结点
                List<String> children = client.getChildren().forPath(basePath);
                List<String> sortedList = Lists.newArrayList(children);
                Collections.sort(sortedList);
                String selfNode = ZKPaths.getNodeFromPath(selfPath);
                //获取当前候选人所在的位置
                int selfIndex = sortedList.indexOf(selfNode);
                if (selfIndex < 0) {
                    throw new IllegalMonitorStateException();
                }
                //如果是第一个结点，则获得锁
                if (selfIndex == 0) {
                    getLock = true;
                    break;
                }
                //监控前一个结点，当前一个结点释放锁时，当前候选人获取锁
                synchronized (this) {
                    String previousSequencePath = ZKPaths.makePath(basePath, sortedList.get(selfIndex - 1));
                    log.info("watch {}", previousSequencePath);
                    client.getData().usingWatcher(watcher).forPath(previousSequencePath);
                    if (waitTime == 0) {
                        break;
                    } else if (waitTime > 0) {
                        long diff = System.currentTimeMillis() - startTime;
                        if (diff < waitTime) {
                            wait(waitTime - diff);
                        } else {
                            deleteSelf();
                            break;
                        }
                    } else {
                        wait();
                        log.info("wake up");
                    }
                }
            } catch (Exception e) {
                deleteSelf();
                throw new RuntimeException(e);
            }
        }
        //记录线程获取锁的上下文
        if (getLock) {
            threadLocal.set(new ThreadContext(1));
        }
        return getLock;
    }

    private void deleteSelf() {
        try {
            client.delete().guaranteed().forPath(selfPath);
        } catch (Exception e) {
            //ignore
        }
    }

    @Override
    public void unlock() {
        ThreadContext context = threadLocal.get();
        if (context == null) {
            throw new IllegalMonitorStateException("Thread not have lock");
        }
        if (context.lockCount == 1) {
            deleteSelf();
            threadLocal.set(null);
        } else {
            context.lockCount--;
        }
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }
}
