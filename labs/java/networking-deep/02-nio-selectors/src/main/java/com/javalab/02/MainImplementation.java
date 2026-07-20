package com.javalab.02;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MainImplementation {

    public static class NioEchoServer implements AutoCloseable {
        private final Selector selector;
        private final ServerSocketChannel serverChannel;
        private volatile boolean running;
        private final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();

        public NioEchoServer(int port) throws IOException {
            this.selector = Selector.open();
            this.serverChannel = ServerSocketChannel.open();
            this.serverChannel.configureBlocking(false);
            this.serverChannel.socket().bind(new InetSocketAddress(port));
            this.serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            this.running = true;
        }

        public void start() throws IOException {
            while (running) {
                selector.select();
                Runnable task;
                while ((task = tasks.poll()) != null) task.run();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    if (!key.isValid()) continue;
                    if (key.isAcceptable()) accept(key);
                    else if (key.isReadable()) read(key);
                }
            }
        }

        private void accept(SelectionKey key) throws IOException {
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(256));
        }

        private void read(SelectionKey key) throws IOException {
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer buf = (ByteBuffer) key.attachment();
            int read = sc.read(buf);
            if (read == -1) { sc.close(); return; }
            buf.flip();
            sc.write(buf);
            buf.compact();
        }

        public void stop() { running = false; selector.wakeup(); }
        public void close() throws IOException { stop(); serverChannel.close(); selector.close(); }
        public int getPort() { return serverChannel.socket().getLocalPort(); }
    }
}
