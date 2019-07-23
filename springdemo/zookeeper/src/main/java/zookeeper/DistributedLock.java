package zookeeper;

import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class DistributedLock implements Lock {

    private final CuratorFramework curatorFramework;
    private final String basePath;
    private String selfPath;

    private final Watcher watcher = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            synchronized (this) {
                notifyAll();
            }
        }
    };

    public DistributedLock(CuratorFramework curatorFramework, String basePath) {
        this.curatorFramework = curatorFramework;
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
        long startTime = System.currentTimeMillis();
        long waitTime = unit.toMillis(time);
        boolean getLock = false;
        //第一次加锁
        if (selfPath == null) {
            try {
                selfPath = curatorFramework
                        .create()
                        .creatingParentsIfNeeded()
                        .withProtection()
                        .withMode(CreateMode.EPHEMERAL)
                        .forPath(basePath);
            } catch (Exception exception) {
                deleteSelf();
                throw new RuntimeException(exception);
            }
        }
        while (true) {
            try {
                //获取所有的序列结点
                List<String> children = curatorFramework.getChildren().forPath(basePath);
                List<String> sortedList = Lists.newArrayList(children);
                Collections.sort(sortedList);
                int selfIndex = sortedList.indexOf(selfPath);
                if (selfIndex < 0) {
                    throw new IllegalMonitorStateException();
                }
                //如果是第一个结点，则获得锁
                if (selfIndex == 0) {
                    getLock = true;
                    break;
                }
                //监控前一个结点
                synchronized (this) {
                    String previousSequencePath = sortedList.get(selfIndex - 1);
                    curatorFramework.getData().usingWatcher(watcher).forPath(previousSequencePath);
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
                    }
                }
            } catch (Exception e) {
                deleteSelf();
                throw new RuntimeException(e);
            }
        }
        return getLock;
    }

    private void deleteSelf() {
        try {
            curatorFramework.delete().guaranteed().forPath(selfPath);
        } catch (Exception e) {
            //ignore
        }
    }

    @Override
    public void unlock() {

    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }
}
