package com.networking.deep.lab01;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class Http2DeepDive {

    public record Frame(byte type, byte flags, int streamId, byte[] payload) {
        public static final byte DATA = 0, HEADERS = 1, PRIORITY = 2, RST_STREAM = 3,
            SETTINGS = 4, PUSH_PROMISE = 5, PING = 6, GOAWAY = 7, WINDOW_UPDATE = 8;
    }

    public record HpackHeader(String name, String value, boolean indexed) {}

    public record Stream(int id, int priority, int weight, int dependency, boolean exclusive, StreamState state) {
        public enum StreamState { IDLE, OPEN, HALF_CLOSED, CLOSED }
    }

    public static class HpackEncoder {
        private final Map<String, String> dynamicTable = new LinkedHashMap<>(100, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) { return size() > 100; }
        };

        public List<byte[]> encode(List<HpackHeader> headers) {
            var result = new ArrayList<byte[]>();
            for (var h : headers) {
                if (h.indexed()) {
                    result.add(encodeIndexed(h));
                } else {
                    dynamicTable.put(h.name(), h.value());
                    result.add(encodeLiteral(h));
                }
            }
            return result;
        }

        private byte[] encodeIndexed(HpackHeader h) { return new byte[] { (byte) 0x80 }; }
        private byte[] encodeLiteral(HpackHeader h) { return (h.name() + ": " + h.value()).getBytes(); }

        public int dynamicTableSize() { return dynamicTable.size(); }
    }

    public static class Http2Connection {
        private final AtomicInteger streamCounter = new AtomicInteger(1);
        private final Map<Integer, Stream> streams = new ConcurrentHashMap<>();
        private final AtomicInteger connectionWindow = new AtomicInteger(65535);
        private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        public int openStream(int priority, int weight, int dependency) {
            int id = streamCounter.getAndAdd(2);
            var stream = new Stream(id, priority, weight, dependency, false, Stream.StreamState.OPEN);
            streams.put(id, stream);
            System.out.println("Opened stream " + id + " (priority=" + priority + ", weight=" + weight + ")");
            return id;
        }

        public boolean sendData(int streamId, byte[] data) {
            var stream = streams.get(streamId);
            if (stream == null || stream.state() == Stream.StreamState.CLOSED) return false;
            int frameCount = (data.length + 16383) / 16384;
            for (int i = 0; i < frameCount; i++) {
                int offset = i * 16384;
                int len = Math.min(16384, data.length - offset);
                var frame = new Frame(Frame.DATA, (byte) (i == frameCount - 1 ? 1 : 0), streamId, Arrays.copyOfRange(data, offset, offset + len));
                processFrame(frame);
            }
            return true;
        }

        public void processFrame(Frame frame) {
            switch (frame.type()) {
                case Frame.DATA -> System.out.println("  DATA frame: stream=" + frame.streamId() + " size=" + frame.payload().length);
                case Frame.HEADERS -> System.out.println("  HEADERS frame: stream=" + frame.streamId());
                case Frame.PUSH_PROMISE -> System.out.println("  PUSH_PROMISE: stream=" + frame.streamId());
                case Frame.WINDOW_UPDATE -> connectionWindow.addAndGet(frame.payload().length > 0 ? frame.payload()[0] : 0);
                case Frame.RST_STREAM -> closeStream(frame.streamId());
            }
        }

        public void closeStream(int id) {
            var stream = streams.get(id);
            if (stream != null) {
                streams.put(id, new Stream(id, stream.priority(), stream.weight(), stream.dependency(), stream.exclusive(), Stream.StreamState.CLOSED));
            }
        }

        public void shutdown() { executor.shutdown(); }
    }

    public static class ServerPush {
        private final Http2Connection conn;
        private final Map<Integer, String> pushedResources = new ConcurrentHashMap<>();

        public ServerPush(Http2Connection conn) { this.conn = conn; }

        public void pushResource(int originalStreamId, String resourcePath) {
            int pushStreamId = conn.openStream(0, 1, originalStreamId);
            pushedResources.put(pushStreamId, resourcePath);
            System.out.println("Server push: " + resourcePath + " on stream " + pushStreamId);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== HTTP/2 Multiplexing ===");
        var conn = new Http2Connection();
        var s1 = conn.openStream(0, 16, 0);
        var s2 = conn.openStream(1, 8, s1);
        var s3 = conn.openStream(2, 4, s2);
        conn.sendData(s1, "GET /api/users HTTP/1.1\r\nHost: example.com".getBytes());
        conn.sendData(s2, "GET /api/products HTTP/1.1\r\nHost: example.com".getBytes());
        conn.sendData(s3, "GET /api/orders HTTP/1.1\r\nHost: example.com".getBytes());

        System.out.println("\n=== Server Push ===");
        var push = new ServerPush(conn);
        push.pushResource(s1, "/styles/main.css");
        push.pushResource(s1, "/scripts/app.js");

        System.out.println("\n=== HPACK Encoding ===");
        var encoder = new HpackEncoder();
        var headers = List.of(
            new HpackHeader(":method", "GET", true),
            new HpackHeader(":path", "/index.html", false),
            new HpackHeader(":authority", "example.com", false),
            new HpackHeader("accept-encoding", "gzip", false)
        );
        var encoded = encoder.encode(headers);
        System.out.println("HPACK encoded " + headers.size() + " headers into " + encoded.size() + " blocks");
        System.out.println("Dynamic table size: " + encoder.dynamicTableSize());
    }
}
