/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package mybatis.demo;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.*;

public class AppTest {

    public static void main(String[] args) throws Exception{
        ReentrantLock lock = new ReentrantLock(false);
        lock.lock();
        Thread.sleep(1000);
        new Thread(()->{
            System.out.println(111);
            lock.lock();
            System.out.println(222);
            lock.unlock();
        }).start();
        Thread.sleep(1000);
        new Thread(()->{
            System.out.println(111);
            lock.lock();
        }).start();
        Thread.sleep(1000);
        lock.unlock();
    }
}
