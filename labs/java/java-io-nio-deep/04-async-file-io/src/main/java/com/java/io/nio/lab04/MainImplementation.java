package com.java.io.nio.lab04;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Async I/O — Main Implementation
 *
 * Demonstrates: AsynchronousFileChannel with Future and CompletionHandler,
 * AsynchronousSocketChannel, async channel groups.
 */
public class MainImplementation {

    /**
     * Async file read using Future-based API.
     */
    public String asyncReadFuture(String path) throws IOException, InterruptedException, ExecutionException {
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                Path.of(path), StandardOpenOption.READ)) {

            ByteBuffer buf = ByteBuffer.allocate((int) channel.size());
            Future<Integer> result = channel.read(buf, 0);
            int bytesRead = result.get();  // wait for completion
            buf.flip();
            return StandardCharsets.UTF_8.decode(buf).toString();
        }
    }

    /**
     * Async file write using Future-based API.
     */
    public void asyncWriteFuture(String path, String content) throws IOException, InterruptedException, ExecutionException {
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                Path.of(path), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {

            ByteBuffer buf = ByteBuffer.wrap(content.getBytes(StandardCharsets.UTF_8));
            Future<Integer> result = channel.write(buf, 0);
            result.get();  // wait for completion
        }
    }

    /**
     * Async file read using CompletionHandler callback.
     */
    public CompletableFuture<String> asyncReadCallback(String path) throws IOException {
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                Path.of(path), StandardOpenOption.READ);

        ByteBuffer buf = ByteBuffer.allocate((int) channel.size());
        CompletableFuture<String> promise = new CompletableFuture<>();

        channel.read(buf, 0, buf, new CompletionHandler<>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                attachment.flip();
                String content = StandardCharsets.UTF_8.decode(attachment).toString();
                try { channel.close(); } catch (IOException e) { /* ignore */ }
                promise.complete(content);
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try { channel.close(); } catch (IOException e) { /* ignore */ }
                promise.completeExceptionally(exc);
            }
        });

        return promise;
    }

    /**
     * Custom AsynchronousChannelGroup with specific thread pool.
     */
    public AsynchronousChannelGroup createCustomGroup(int poolSize) throws IOException {
        ExecutorService pool = Executors.newFixedThreadPool(poolSize, r -> {
            Thread t = new Thread(r, "async-io-" + poolSize);
            t.setDaemon(true);
            return t;
        });
        return AsynchronousChannelGroup.withThreadPool(pool);
    }

    public static void main(String[] args) throws Exception {
        MainImplementation m = new MainImplementation();

        // Future-based async file write and read
        Path tmp = Files.createTempFile("async-future", ".txt");
        tmp.toFile().deleteOnExit();
        m.asyncWriteFuture(tmp.toString(), "Async Future Test");
        String content = m.asyncReadFuture(tmp.toString());
        assert content.equals("Async Future Test") : "Future read mismatch: " + content;

        // CompletionHandler-based async read
        Path tmp2 = Files.createTempFile("async-cb", ".txt");
        tmp2.toFile().deleteOnExit();
        Files.writeString(tmp2, "Callback Test");
        String cbContent = m.asyncReadCallback(tmp2.toString()).get();
        assert cbContent.equals("Callback Test") : "Callback read mismatch: " + cbContent;

        // Custom executor service used with AsynchronousFileChannel
        ExecutorService customPool = Executors.newFixedThreadPool(4, r -> {
            Thread t = new Thread(r, "async-io-pool");
            t.setDaemon(true);
            return t;
        });
        try {
            Path tmp3 = Files.createTempFile("async-group", ".txt");
            tmp3.toFile().deleteOnExit();
            try (AsynchronousFileChannel ch = AsynchronousFileChannel.open(
                    tmp3, Set.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE), customPool)) {
                ByteBuffer buf = ByteBuffer.wrap("Group test".getBytes(StandardCharsets.UTF_8));
                ch.write(buf, 0).get();
            }
        } finally {
            customPool.shutdown();
        }

        System.out.println("All Async I/O tests passed.");
    }
}
