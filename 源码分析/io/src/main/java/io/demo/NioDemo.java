package io.demo;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

@Slf4j
public class NioDemo {
    private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

    private void start() throws Exception {
        //开启一个Server Channel
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress("127.0.0.1", 8080));
        Selector selector = Selector.open();
        //绑定Server Chanel 到选择器，类型是接受类型
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterable = keys.iterator();
            while (iterable.hasNext()) {
                SelectionKey key = iterable.next();
                if (!key.isValid()) {
                    continue;
                }
                //有新的请求时，将请求的Channel绑定到选择器
                if (key.isAcceptable()) {
                    ServerSocketChannel workChannel = (ServerSocketChannel) key.channel();
                    SocketChannel workClient = workChannel.accept();
                    log.info("Accept from {}", workClient.getRemoteAddress());
                    workClient.configureBlocking(false);
                    workClient.register(selector, SelectionKey.OP_WRITE);
                }
                if (key.isWritable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    byteBuffer.clear();
                    byteBuffer.put("Hello World".getBytes());
                    byteBuffer.flip();
                    channel.write(byteBuffer);
                    channel.close();
                }
                iterable.remove();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        NioDemo demo = new NioDemo();
        demo.start();
    }
}
