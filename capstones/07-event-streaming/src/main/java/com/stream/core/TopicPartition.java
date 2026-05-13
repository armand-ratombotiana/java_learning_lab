package com.stream.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
@Slf4j
public class TopicPartition {

    private final String topic;
    private final int partitionId;
    private final Path dataDir;
    private final List<LogSegment> segments;
    private final ReentrantLock writeLock;

    private long highWatermark;

    public TopicPartition(String topic, int partitionId, Path dataDir) throws IOException {
        this.topic = topic;
        this.partitionId = partitionId;
        this.dataDir = dataDir;
        this.segments = new ArrayList<>();
        this.writeLock = new ReentrantLock();

        Files.createDirectories(dataDir);
        loadSegments();

        if (segments.isEmpty()) {
            segments.add(new LogSegment(topic, partitionId, 0, dataDir));
        }

        log.info("Initialized topic-partition: {}-{}", topic, partitionId);
    }

    private void loadSegments() throws IOException {
        String pattern = String.format("%s-%d-*.log", topic, partitionId);
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);

        Files.walk(dataDir)
            .filter(Files::isRegularFile)
            .filter(matcher::matches)
            .sorted()
            .forEach(path -> {
                try {
                    long baseOffset = extractBaseOffset(path.getFileName().toString());
                    segments.add(new LogSegment(topic, partitionId, baseOffset, dataDir));
                } catch (IOException e) {
                    log.error("Failed to load segment: {}", path, e);
                }
            });
    }

    private long extractBaseOffset(String fileName) {
        String[] parts = fileName.replace(".log", "").split("-");
        return Long.parseLong(parts[parts.length - 1]);
    }

    public synchronized LogSegment.AppendResult append(byte[] key, byte[] value) {
        writeLock.lock();
        try {
            LogSegment activeSegment = segments.get(segments.size() - 1);
            LogSegment.AppendResult result = activeSegment.append(key, value);

            if (result.offset() == -1) {
                long newBaseOffset = activeSegment.nextOffset();
                LogSegment newSegment = new LogSegment(topic, partitionId, newBaseOffset, dataDir);
                segments.add(newSegment);
                result = newSegment.append(key, value);
            }

            if (result.success()) {
                highWatermark = Math.max(highWatermark, result.offset() + 1);
            }

            return result;
        } finally {
            writeLock.unlock();
        }
    }

    public List<LogSegment.Record> read(long offset, int maxRecords) {
        for (LogSegment segment : segments) {
            if (offset >= segment.baseOffset() && offset < segment.nextOffset()) {
                return segment.read(offset, maxRecords);
            }
        }
        return List.of();
    }

    public long endOffset() {
        return segments.isEmpty() ? 0 : segments.get(segments.size() - 1).nextOffset();
    }

    public String getTopic() { return topic; }
    public int getPartitionId() { return partitionId; }
    public long getHighWatermark() { return highWatermark; }

    public record AppendResult(long offset, boolean success) {}
}