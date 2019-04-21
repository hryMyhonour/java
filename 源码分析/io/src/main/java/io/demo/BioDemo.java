package io.demo;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class BioDemo {

    /**
     * Bio是阻塞型IO，用多线程处理请求可以达到伪异步处理请求的效果，
     * 可以用线程池来控制开启线程的数量并复用线程，但是从底层上来讲，
     * 仍然是同步阻塞的，即等待操作系统做出回复，如果请求长时间在线程池里没被应答，
     * TCP连接就会一直等待排队，接受窗口就会不断变小，直到0发送方不会再发送消息。
     *
     * @throws Exception
     */
    private void serverWithPool() throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);
        ExecutorService pool = Executors.newFixedThreadPool(10);
        while (true) {
            Socket socket = serverSocket.accept();
            pool.submit(() -> this.response(socket));
        }
    }

    /**
     * 如果每个处理都新建一个线程，而且处理又需要一定的时间时，则可能会导致资源耗尽。
     * 例如，JVM建立虚拟机栈需要内存资源，当线程数量到达一数量时，无法分配内存可能会抛出OOM
     *
     * @throws Exception
     */
    private void serverInThread() throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);
        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(() -> {
                this.response(socket);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void workingInOneThread() throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);
        while (true) {
            Socket socket = serverSocket.accept();
            response(socket);
        }
    }

    private void response(Socket socket) {
        log.info("Accept from ip: {}, port: {}", socket.getInetAddress().getHostAddress(), socket.getPort());
        try (OutputStream outputStream = socket.getOutputStream()) {
            outputStream.write("Hello World".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        BioDemo demo = new BioDemo();
        //demo.serverWithPool();
        //demo.serverInThread();
        demo.workingInOneThread();
    }
}
