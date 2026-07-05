package com.net.http;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class Http2Multiplexing {

    public static class Http2Stream {
        private final int streamId;
        private final String path;
        private volatile String response;
        private volatile boolean completed;

        public Http2Stream(int streamId, String path) {
            this.streamId = streamId;
            this.path = path;
        }

        public int getStreamId() { return streamId; }
        public String getPath() { return path; }
        public boolean isCompleted() { return completed; }

        public void complete(String resp) {
            this.response = resp;
            this.completed = true;
        }
    }

    public static class Http2Connection {
        private final AtomicInteger streamCounter = new AtomicInteger(1);
        private final Map<Integer, Http2Stream> streams = new ConcurrentHashMap<>();
        private final ExecutorService workerPool = Executors.newFixedThreadPool(3);

        public Http2Stream openStream(String path) {
            int id = streamCounter.getAndAdd(2);
            Http2Stream stream = new Http2Stream(id, path);
            streams.put(id, stream);
            System.out.println("Opened stream " + id + " for " + path);

            workerPool.submit(() -> {
                try {
                    Thread.sleep((long) (Math.random() * 500));
                    stream.complete("200 OK - Response for " + path);
                    System.out.println("Stream " + id + " completed");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            return stream;
        }

        public boolean allCompleted() {
            return streams.values().stream().allMatch(Http2Stream::isCompleted);
        }

        public void awaitAll() throws Exception {
            while (!allCompleted()) {
                Thread.sleep(50);
            }
        }

        public void shutdown() { workerPool.shutdown(); }
    }

    public static void main(String[] args) throws Exception {
        Http2Connection conn = new Http2Connection();

        System.out.println("=== HTTP/2 Multiplexing ===");
        Http2Stream s1 = conn.openStream("/api/users");
        Http2Stream s2 = conn.openStream("/api/products");
        Http2Stream s3 = conn.openStream("/api/orders");

        conn.awaitAll();

        System.out.println("\nAll responses:");
        for (Http2Stream s : Arrays.asList(s1, s2, s3)) {
            System.out.println("  Stream " + s.getStreamId() + " (" + s.getPath() + "): " + s.response);
        }

        conn.shutdown();
    }
}
