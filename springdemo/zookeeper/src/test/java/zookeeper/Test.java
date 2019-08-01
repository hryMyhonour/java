package zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

public class Test {

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory
                .builder()
                .connectString("94hry.tech:2181")
                .retryPolicy(retryPolicy)
                .namespace("testzookeeper")
                .build();
        client.start();
        String path = client
                .create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath("/lock");
        List<String> children = client.getChildren().forPath("/");
        System.out.println(path);
        System.out.println(children);
    }
}
